package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository
@Qualifier("db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
        properties.put(Environment.HBM2DDL_AUTO, "update");
        properties.put(Environment.AUTOCOMMIT, "false");
        properties.put(Environment.HIGHLIGHT_SQL, "true");
        properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.FORMAT_SQL, "true");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        Configuration configuration = new Configuration();
        configuration.addProperties(properties);
        configuration.addAnnotatedClass(Player.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            try {
                String SQL = "SELECT * FROM player";
                List<Player> list = session.createNativeQuery(SQL, Player.class).list();
                t.commit();
                return list;
            } catch (Exception e) {
                t.rollback();
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            try {
                int playersCount = (Integer) session.createNamedQuery("getPlayersCount").getSingleResult();
                t.commit();
                return playersCount;
            } catch (Exception e) {
                t.rollback();
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            try {
                session.persist(player);
                t.commit();
            } catch (Exception e) {
                t.rollback();
                throw new RuntimeException(e);
            }
            return player;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            try {
                session.merge(player);
                t.commit();
            } catch (Exception e) {
                t.rollback();
                throw new RuntimeException(e);
            }
            return player;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Player player = session.get(Player.class, id);
            if (player != null) {
                return Optional.of(player);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            try {
                session.remove(player);
                t.commit();
            } catch (Exception e) {
                t.rollback();
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}