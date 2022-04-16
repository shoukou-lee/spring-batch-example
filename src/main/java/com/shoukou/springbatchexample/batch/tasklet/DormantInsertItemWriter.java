package com.shoukou.springbatchexample.batch.tasklet;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.shoukou.springbatchexample.model.DormantUser;
import com.shoukou.springbatchexample.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.util.Assert;

public class DormantInsertItemWriter implements ItemWriter<User>, InitializingBean {
    protected static final Log logger = LogFactory.getLog(JpaItemWriter.class);
    private EntityManagerFactory entityManagerFactory;
    private boolean usePersist = false;

    public DormantInsertItemWriter() {
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void setUsePersist(boolean usePersist) {
        this.usePersist = usePersist;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.entityManagerFactory, "An EntityManagerFactory is required");
    }

    public void write(List<? extends User> items) {
        EntityManager entityManager = EntityManagerFactoryUtils.getTransactionalEntityManager(this.entityManagerFactory);
        if (entityManager == null) {
            throw new DataAccessResourceFailureException("Unable to obtain a transactional EntityManager");
        } else {
            this.doWrite(entityManager, items);
            entityManager.flush();
        }
    }

    protected void doWrite(EntityManager entityManager, List<? extends User> items) {
        if (logger.isDebugEnabled()) {
            logger.debug("Writing to JPA with " + items.size() + " items.");
        }

        if (!items.isEmpty()) {
            long addedToContextCount = 0L;
            Iterator var5 = items.iterator();

            while(var5.hasNext()) {
                DormantUser item = new DormantUser((User) var5.next());
                if (!entityManager.contains(item)) {
                    if (this.usePersist) {
                        entityManager.persist(item);
                    } else {
                        entityManager.merge(item);
                    }

                    ++addedToContextCount;
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug(addedToContextCount + " entities " + (this.usePersist ? " persisted." : "merged."));
                logger.debug((long)items.size() - addedToContextCount + " entities found in persistence context.");
            }
        }

    }
}
