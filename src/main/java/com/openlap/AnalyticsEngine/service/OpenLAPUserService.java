package com.openlap.AnalyticsEngine.service;

import com.openlap.AnalyticsEngine.model.OpenLapUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionManager;
import java.util.List;

@Service
public class OpenLAPUserService {
    TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
    EntityManagerFactory factory = Persistence.createEntityManagerFactory("OpenLAP");
    EntityManager em = factory.createEntityManager();

    OpenLapUser loadUserByUsername(String email) {

        OpenLapUser openLapUser = em.find( OpenLapUser.class, email);
        //User user = userRepo.findByEmail(email);
        if (openLapUser == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return openLapUser;
    }
}
