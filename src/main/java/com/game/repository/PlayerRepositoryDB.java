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
        fillingDB(query);
    }

    private void fillingDB(Query query) {
        if (query.list().isEmpty()) {
            for (Player player : PlayerRepositoryMemory.getPlayerList()) {
                save(player);
            }
        }
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = MySessionFactory.getSessionFactory().openSession()) {
            NativeQuery<Player> players = session.createNativeQuery("SELECT * FROM player", Player.class);
            players.setFirstResult(pageNumber * pageSize);
            players.setMaxResults(pageSize);
            return players.list();
        }

    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createNamedQuery("getAllCount",Long.class);
            return Math.toIntExact(query.uniqueResult());
        }

    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                session.save(player);
                session.getTransaction().commit();
                return player;
            } catch (RuntimeException e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                session.update(player);
                session.getTransaction().commit();
                return player;
            } catch (RuntimeException e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Player player = session.find(Player.class, id);
            return Optional.ofNullable(player);
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                session.remove(player);
                session.getTransaction().commit();
            } catch (RuntimeException e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}