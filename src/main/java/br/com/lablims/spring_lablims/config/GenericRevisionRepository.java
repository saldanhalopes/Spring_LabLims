package br.com.lablims.spring_lablims.config;


import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.config.EntityRevision;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
@Transactional
public class GenericRevisionRepository<T> {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditReader getAuditReader() {
        return AuditReaderFactory.get(entityManager);
    }

    public List<EntityRevision<T>> listaRevisoes(Class<T> tipo) {
        List<EntityRevision<T>> allRevisions = new ArrayList<>();
        AuditQuery queryHistoryOfUserWithRev = getAuditReader().createQuery()
                .forRevisionsOfEntity(tipo, false, true);
        List lstHistoryWithRev = queryHistoryOfUserWithRev.getResultList();

        for (Object item : lstHistoryWithRev) {
            RevisionType revisionType = (RevisionType) ((Object[]) item)[2];
            T listaRevisoes = (T) ((Object[]) item)[0];
            CustomRevisionEntity r = (CustomRevisionEntity) ((Object[]) item)[1];
            allRevisions.add(new EntityRevision(new CustomRevisionEntity(r.getId(), r.getRevisionDate(),
                    r.getUserName(), r.getIp(), revisionType.toString(), r.getMotivo()), listaRevisoes));
        }
        return allRevisions;
    }

    public List<EntityRevision<T>> listaRevisoesById(Integer id, Class<T> tipo) {
        List<EntityRevision<T>> allRevisions = new ArrayList<>();

        AuditQuery queryHistoryOfUserWithRev = getAuditReader().createQuery()
                .forRevisionsOfEntity(tipo, false, true)
                .add(AuditEntity.property("id").eq(id));
        List lstHistoryOfUserWithRev = queryHistoryOfUserWithRev.getResultList();

        for (Object item : lstHistoryOfUserWithRev) {
            RevisionType revisionType = (RevisionType) ((Object[]) item)[2];
            T listaRevisoes = (T) ((Object[]) item)[0];
            CustomRevisionEntity r = (CustomRevisionEntity) ((Object[]) item)[1];
            allRevisions.add(new EntityRevision(new CustomRevisionEntity(r.getId(), r.getRevisionDate(),
                    r.getUserName(), r.getIp(), revisionType.toString(), r.getMotivo()), listaRevisoes));
        }
        return allRevisions;
    }

}
