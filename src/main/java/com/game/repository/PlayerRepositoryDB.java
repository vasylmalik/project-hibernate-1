package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "mysql");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration()
                .addAnnotatedClass(Player.class)
                .addProperties(properties)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Player> nativeQuery = session.createNativeQuery(
                    "SELECT * FROM rpg.player",
                    Player.class);
            nativeQuery.setFirstResult(pageNumber * pageSize);
            nativeQuery.setMaxResults(pageSize);
            return nativeQuery.list();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> playersCount = session.createNamedQuery("Get_All_Players_Count", Long.class);
            return Math.toIntExact(playersCount.getSingleResult());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.save(player);
                transaction.commit();
                return player;
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Player mergedPlayer = (Player) session.merge(player);
                transaction.commit();
                return mergedPlayer;
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Player.class, id));
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.remove(player);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}