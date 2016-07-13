/**
 * 
 */
package com.thinkbiganalytics.metadata.modeshape.sla;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.thinkbiganalytics.metadata.modeshape.MetadataRepositoryException;
import com.thinkbiganalytics.metadata.modeshape.common.AbstractJcrAuditableSystemEntity;
import com.thinkbiganalytics.metadata.modeshape.common.JcrEntity;
import com.thinkbiganalytics.metadata.modeshape.support.JcrPropertyUtil;
import com.thinkbiganalytics.metadata.modeshape.support.JcrUtil;
import com.thinkbiganalytics.metadata.sla.api.Obligation;
import com.thinkbiganalytics.metadata.sla.api.ObligationGroup;
import com.thinkbiganalytics.metadata.sla.api.ServiceLevelAgreement;

/**
 *
 * @author Sean Felten
 */
public class JcrServiceLevelAgreement extends AbstractJcrAuditableSystemEntity implements ServiceLevelAgreement, Serializable {


    private static final long serialVersionUID = 2611479261936214396L;

    public static final String DESCRIPTION = "jcr:description";
    public static final String NAME = "jcr:title";
    public static final String DEFAULT_GROUP = "tba:defaultGroup";
    public static final String GROUPS = "tba:groups";
    
    public static final String GROUP_TYPE = "tba:obligationGroup";

    /**
     * 
     */
    public JcrServiceLevelAgreement(Node node) {
        super(node);
    }

    /* (non-Javadoc)
     * @see com.thinkbiganalytics.metadata.sla.api.ServiceLevelAgreement#getId()
     */
    @Override
    public SlaId getId() {
        try {
            return new SlaId(getObjectId());
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Failed to retrieve the SLA ID", e);
        }
    }

    /* (non-Javadoc)
     * @see com.thinkbiganalytics.metadata.sla.api.ServiceLevelAgreement#getName()
     */
    @Override
    public String getName() {
        return JcrPropertyUtil.getString(this.node, NAME);
    }

    /* (non-Javadoc)
     * @see com.thinkbiganalytics.metadata.sla.api.ServiceLevelAgreement#getDescription()
     */
    @Override
    public String getDescription() {
        return JcrPropertyUtil.getString(this.node, DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see com.thinkbiganalytics.metadata.sla.api.ServiceLevelAgreement#getObligationGroups()
     */
    @Override
    public List<ObligationGroup> getObligationGroups() {
        try {
            @SuppressWarnings("unchecked")
            Iterator<Node> defItr = (Iterator<Node>) this.node.getNodes(DEFAULT_GROUP);
            @SuppressWarnings("unchecked")
            Iterator<Node> grpItr = (Iterator<Node>) this.node.getNodes(GROUPS);
            
            return Lists.newArrayList(Iterators.concat(
                Iterators.transform(defItr, (groupNode) -> {
                    return JcrUtil.createJcrObject(groupNode, JcrObligationGroup.class, JcrServiceLevelAgreement.this);
                }),
                Iterators.transform(grpItr, (groupNode) -> {
                    return JcrUtil.createJcrObject(groupNode, JcrObligationGroup.class, JcrServiceLevelAgreement.this);
                })));
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Failed to retrieve the obligation nodes", e);
        }
        
    }

    /* (non-Javadoc)
     * @see com.thinkbiganalytics.metadata.sla.api.ServiceLevelAgreement#getObligations()
     */
    @Override
    public List<Obligation> getObligations() {
        List<Obligation> list = new ArrayList<>();
        
        for (ObligationGroup group : getObligationGroups()) {
            list.addAll(group.getObligations());
        }
        
        return list;
    }
    
    public JcrObligationGroup getDefaultGroup() {
        return JcrUtil.getOrCreateNode(this.node, DEFAULT_GROUP, GROUP_TYPE, JcrObligationGroup.class, JcrServiceLevelAgreement.this);
    }

    public void addGroup(JcrObligationGroup group) {
        try {
            this.node.addNode(GROUPS, GROUP_TYPE);
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Failed to create the obligation group node", e);
        }
    }


    public static class SlaId extends JcrEntity.EntityId implements ServiceLevelAgreement.ID {

        public SlaId(Serializable ser) {
            super(ser);
        }
    }
}