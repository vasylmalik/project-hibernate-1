package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        this.sessionFactory = MySessionFactory.getSessionFactory();
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("select name from Player");
        if (query.list().isEmpty()) {
            for (Player player : PlayerRepositoryMemory.storage) {
                save(player);
            }
        }

    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = MySessionFactory.getSessionFactory().openSession()) {
            NativeQuery<Player> players = session.createNativeQuery("SELECT * FROM player", Player.class);
//            players.setFirstResult((pageNumber + 1) * pageSize - pageSize);
            players.setFirstResult(pageNumber * pageSize);
            players.setMaxResults(pageSize);
            return players.list();
       /* return players.list().stream()
                .sorted(Comparator.comparingLong(Player::getId))
                .skip((long) pageNumber * pageSize)
                .limit(pageSize).collect(Collectors.toList());*/
        }

    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            //Это не работает.
      /*  Query<Integer> query = session.createQuery("select count (*) from Player",Integer.class);
        return query.uniqueResult();*/
            Query allPlayersCount = session.createQuery("from Player");
            return allPlayersCount.list().size();
        }

    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(player);
            transaction.commit();
            return player;
        }
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            System.out.println("update");
            session.merge(player);
            transaction.commit();
            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Player player = session.find(Player.class, id);
            transaction.commit();
            return Optional.ofNullable(player);
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(player);
            transaction.commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}