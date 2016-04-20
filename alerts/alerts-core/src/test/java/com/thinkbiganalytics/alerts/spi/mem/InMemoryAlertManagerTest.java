package com.thinkbiganalytics.alerts.spi.mem;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.util.concurrent.MoreExecutors;
import com.thinkbiganalytics.alerts.api.Alert;
import com.thinkbiganalytics.alerts.api.Alert.Level;
import com.thinkbiganalytics.alerts.spi.AlertDescriptor;
import com.thinkbiganalytics.alerts.spi.AlertNotifyReceiver;

public class InMemoryAlertManagerTest {
    
    @Mock
    private AlertNotifyReceiver receiver;
    
    private InMemoryAlertManager manager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        this.manager = new InMemoryAlertManager();
        this.manager.setReceiversExecutor(MoreExecutors.directExecutor());
        this.manager.addReceiver(this.receiver);
    }
    
    @Test
    public void testAddDescriptor() {
        URI type = URI.create("urn:alert:test");
        Map<Alert.State, String> states = new HashMap<>();
        states.put(Alert.State.UNHANDLED, "application/json");
        states.put(Alert.State.HANDLED, "application/json");
        AlertDescriptor descr = new AlertDescriptor(URI.create("urn:alert:test"), "application/json", "description", true, states);
        
        assertThat(this.manager.addDescriptor(descr)).isTrue();
        
        Set<AlertDescriptor> descrSet = this.manager.getAlertDescriptors();
        
        assertThat(descrSet).hasSize(1).extracting("alertType").contains(type);
        assertThat(descrSet).extracting("contentType").contains("application/json");
        assertThat(descrSet).extracting("description").contains("description");
        assertThat(descrSet).extracting("respondable").contains(true);
    }

    @Test
    public void testCreate() {
        URI type = URI.create("urn:alert:test");
        Alert alert = this.manager.create(type, Alert.Level.INFO, "test", "content"); 
        
        assertThat(alert.getType()).isEqualTo(type);
        assertThat(alert.getLevel()).isEqualTo(Alert.Level.INFO);
        assertThat(alert.getDescription()).isEqualTo("test");
        assertThat(alert.getContent()).isEqualTo("content");
    }

    @Test
        public void testResolve() {
        Alert.ID id = this.manager.create(URI.create("urn:alert:test"), Alert.Level.INFO, "test", "content").getId(); 
        
        Alert.ID resolved = this.manager.resolve(id.toString());
        
        assertThat(resolved).isEqualTo(id);
    }

    @Test
    public void testGetAlertById() {
        Alert alert = this.manager.create(URI.create("urn:alert:test"), Alert.Level.INFO, "test", "content"); 
        
        Alert returned = this.manager.getAlert(alert.getId());
        
        assertThat(returned.getId()).isEqualTo(alert.getId());
        assertThat(returned.getType()).isEqualTo(alert.getType());
        assertThat(returned.getLevel()).isEqualTo(alert.getLevel());
    }

    @Test
    public void testGetAlerts() throws InterruptedException {
        this.manager.create(URI.create("urn:alert:test1"), Alert.Level.INFO, "test1", "content"); 
        Thread.sleep(25);
        this.manager.create(URI.create("urn:alert:test2"), Alert.Level.CRITICAL, "test2", "content"); 
        
        Iterator<? extends Alert> itr = this.manager.getAlerts();

        assertThat(itr.hasNext()).isTrue();
        assertThat(itr.next().getLevel()).isEqualTo(Level.INFO);
        assertThat(itr.hasNext()).isTrue();
        assertThat(itr.next().getLevel()).isEqualTo(Level.CRITICAL);
        assertThat(itr.hasNext()).isFalse();
    }

    @Test
    public void testGetAlertsSinceDateTime() throws InterruptedException {
        this.manager.create(URI.create("urn:alert:test1"), Alert.Level.INFO, "test1", "content"); 
        Thread.sleep(25);
        DateTime since = DateTime.now();
        Thread.sleep(25);
        this.manager.create(URI.create("urn:alert:test2"), Alert.Level.CRITICAL, "test2", "content"); 
        Thread.sleep(25);
        
        Iterator<? extends Alert> itr = this.manager.getAlerts(since);

        assertThat(itr.hasNext()).isTrue();
        assertThat(itr.next().getLevel()).isEqualTo(Level.CRITICAL);
        assertThat(itr.hasNext()).isFalse();
    }

    @Test
    public void testGetAlertsSinceID() throws InterruptedException {
        Alert.ID id = this.manager.create(URI.create("urn:alert:test1"), Alert.Level.INFO, "test1", "content").getId(); 
        Thread.sleep(25);
        this.manager.create(URI.create("urn:alert:test2"), Alert.Level.CRITICAL, "test2", "content"); 
        Thread.sleep(25);
        
        Iterator<? extends Alert> itr = this.manager.getAlerts(id);

        assertThat(itr.hasNext()).isTrue();
        assertThat(itr.next().getLevel()).isEqualTo(Level.CRITICAL);
        assertThat(itr.hasNext()).isFalse();
    }
    
    @Test
    public void testRemove() {
        Alert.ID id1 = this.manager.create(URI.create("urn:alert:test1"), Alert.Level.INFO, "test1", "content").getId(); 
        Alert.ID id2 = this.manager.create(URI.create("urn:alert:test2"), Alert.Level.CRITICAL, "test2", "content").getId(); 
        
        Alert alert2 = this.manager.remove(id2);
        Alert alert1 = this.manager.remove(id1);
        Iterator<? extends Alert> itr = this.manager.getAlerts();

        assertThat(itr.hasNext()).isFalse();
        assertThat(alert1.getId()).isEqualTo(id1);
        assertThat(alert2.getId()).isEqualTo(id2);
    }

    @Test
    public void testChangeState() {
        Alert alert = this.manager.create(URI.create("urn:alert:test1"), Alert.Level.INFO, "test1", "created"); 
        
        Alert changed = this.manager.changeState(alert, Alert.State.IN_PROGRESS, "in progress");
        
        Alert retrieved = this.manager.getAlert(alert.getId());
        
        assertThat(changed.getEvents()).hasSize(2).extracting("state").contains(Alert.State.UNHANDLED, Alert.State.IN_PROGRESS);
        assertThat(retrieved.getEvents()).hasSize(2).extracting("state").contains(Alert.State.UNHANDLED, Alert.State.IN_PROGRESS);
    }

    @Test
    public void testNotifyRecieverCreate() {
        this.manager.create(URI.create("urn:alert:test1"), Alert.Level.INFO, "test1", "created"); 
        
        verify(this.receiver).alertsAvailable(any(Integer.class));
    }
    
    @Test
    public void testNotifyRecieverChange() {
        Alert alert = this.manager.create(URI.create("urn:alert:test1"), Alert.Level.INFO, "test1", "created"); 
        this.manager.changeState(alert, Alert.State.IN_PROGRESS, "in progress");
        
        verify(this.receiver, times(2)).alertsAvailable(any(Integer.class));
    }
}