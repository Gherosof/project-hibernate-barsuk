package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

//import javax.annotation.PreDestroy;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
//        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
//        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
//        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
//        properties.put(Environment.HBM2DDL_AUTO, "update");
//        properties.put(Environment.USER, "root");
//        properties.put(Environment.PASS, "0000");

        sessionFactory = new Configuration()
//                .setProperties(properties)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    private SessionFactory sessionFactory;

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        List<Player> result = null;
        String nQuery = "SELECT * FROM player";
        try(Session session = sessionFactory.openSession()) {
            NativeQuery<Player> playerQuery = session.createNativeQuery(nQuery, Player.class);
            playerQuery.setFirstResult(pageNumber);
            playerQuery.setMaxResults(pageSize);
            result = playerQuery.list();
        }
        return result;
    }

    @Override
    public int getAllCount() {
        int result = 0;
        try(Session session = sessionFactory.openSession()) {
            Query<Integer> namedQuery = session.createNamedQuery("Player_GetAllCount", Integer.class);
            result = namedQuery.uniqueResult();
        }
        return result;
    }

    @Override
    public Player save(Player player) {
        Player result = null;
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(player);
            transaction.commit();
        }
        return result;
    }

    @Override
    public Player update(Player player) {
        Player result = null;
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(player);
            transaction.commit();
        }
        return result;
    }

    @Override
    public Optional<Player> findById(long id) {
        Optional<Player> result;
        Player finded = null;
        try(Session session = sessionFactory.openSession()) {
            finded = session.find(Player.class, id);
            result = Optional.ofNullable(finded);
        } catch (Exception e) {
            return Optional.empty();
        }
        return result;
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