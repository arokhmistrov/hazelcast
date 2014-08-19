package com.hazelcast.wm.test;

import com.hazelcast.core.IMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class WebFilterSlowTests extends AbstractWebFilterTest{


    protected WebFilterSlowTests(String serverXml1) {
        super(serverXml1);
    }

    protected WebFilterSlowTests(String serverXml1, String serverXml2) {
        super(serverXml1, serverXml2);
    }

	@Ignore
    @Test
    public void test_github_issue_2887() throws Exception
    {
        CookieStore cookieStore = new BasicCookieStore();
        executeRequest("write", serverPort1, cookieStore);
        executeRequest("read", serverPort2, cookieStore);
        //expire session only on server2
        executeRequest("timeout", serverPort2, cookieStore);

        //Wait till session on server2 is expired
        sleepSeconds(2);

        //send redirect to server2 which has no local session but there is a distributed session.
        HttpResponse resp = request("redirect", serverPort2, cookieStore);

        assertEquals(302, resp.getStatusLine().getStatusCode());
    }

	@Test
	public void test_github_issue_2887_2() throws Exception
	{
		CookieStore cookieStore = new BasicCookieStore();
		//Use case:
		//1) User logged in on server1
		//2) Second request was redirected on server2
		//3) In this case call to request.setSession(false) should return session from the cluster

		//Creates session on server1
		executeRequest("write", serverPort1, cookieStore);

		//Reads value on server 1 (just to check that method works)
		assertEquals("value", executeRequest("readIfExist", serverPort1, cookieStore));

		//Reads value on server 2
		assertEquals("value", executeRequest("readIfExist", serverPort2, cookieStore));
	}

	@Ignore
    @Test(timeout = 60000)
    public void testAttributeRemoval_issue_2618() throws Exception {
        IMap<String, Object> map = hz.getMap("default");
        CookieStore cookieStore = new BasicCookieStore();

        assertEquals("true", executeRequest("write", serverPort1, cookieStore));
        assertEquals(2, map.size());

        assertEquals("value", executeRequest("read", serverPort2, cookieStore));
        assertEquals("true", executeRequest("remove_set_null", serverPort2, cookieStore));
        assertEquals("null", executeRequest("read", serverPort1, cookieStore));
    }

	@Ignore
    @Test(timeout = 60000)
    public void testAttributeNames_issue_2434() throws Exception {
        CookieStore cookieStore = new BasicCookieStore();

        assertEquals("null", executeRequest("read", serverPort1, cookieStore));

        //no name should be created
        assertEquals("", executeRequest("names", serverPort1, cookieStore));
    }

	@Ignore
    @Test(timeout = 60000)
    public void test_github_issue_2187() throws Exception {
        IMap<String, String> map = hz.getMap("default");
        CookieStore cookieStore = new BasicCookieStore();

        assertEquals("null", executeRequest("read", serverPort1, cookieStore));
        assertEquals("true", executeRequest("write", serverPort1, cookieStore));
        assertEquals(2, map.size());

        assertEquals("value", executeRequest("read", serverPort1, cookieStore));
    }

	@Ignore
    @Test(timeout = 60000)
    public void testAttributeDistribution() throws Exception {
        IMap<String, Object> map = hz.getMap("default");
        CookieStore cookieStore = new BasicCookieStore();

        assertEquals("true", executeRequest("write", serverPort1, cookieStore));
        assertEquals(2, map.size());

        assertEquals("value", executeRequest("read", serverPort2, cookieStore));
    }

	@Ignore
    @Test(timeout = 60000)
    public void testAttributeRemoval() throws Exception {
        IMap<String, Object> map = hz.getMap("default");
        CookieStore cookieStore = new BasicCookieStore();

        assertEquals("true", executeRequest("write", serverPort1, cookieStore));
        assertEquals(2, map.size());

        assertEquals("value", executeRequest("read", serverPort2, cookieStore));
        assertEquals("true", executeRequest("remove", serverPort2, cookieStore));
        assertEquals("null", executeRequest("read", serverPort1, cookieStore));
    }

	@Ignore
    @Test(timeout = 60000)
    public void testAttributeUpdate() throws Exception {
        IMap<String, Object> map = hz.getMap("default");
        CookieStore cookieStore = new BasicCookieStore();

        assertEquals("true", executeRequest("write", serverPort1, cookieStore));
        assertEquals(2, map.size());

        assertEquals("value", executeRequest("read", serverPort2, cookieStore));
        assertEquals("true", executeRequest("update", serverPort2, cookieStore));
        assertEquals("value-updated", executeRequest("read", serverPort1, cookieStore));
    }

	@Ignore
    @Test(timeout = 60000)
    public void testAttributeInvalidate() throws Exception {
        IMap<String, Object> map = hz.getMap("default");
        CookieStore cookieStore = new BasicCookieStore();

        assertEquals("true", executeRequest("write", serverPort1, cookieStore));
        assertEquals(2, map.size());

        assertEquals("value", executeRequest("read", serverPort2, cookieStore));

        assertEquals("true", executeRequest("invalidate", serverPort2, cookieStore));
        assertTrue(map.isEmpty());
    }

	@Ignore
    @Test(timeout = 60000)
    public void testAttributeReloadSession() throws Exception {
        IMap<String, Object> map = hz.getMap("default");
        CookieStore cookieStore = new BasicCookieStore();

        assertEquals("true", executeRequest("write", serverPort1, cookieStore));
        assertEquals(2, map.size());

        String oldSessionId = findHazelcastSessionId(map);
        assertNotNull(oldSessionId);

        assertEquals("value", executeRequest("read", serverPort2, cookieStore));
        assertEquals("true", executeRequest("reload", serverPort2, cookieStore));

        String newSessionId = findHazelcastSessionId(map);
        assertNotEquals("The old and new session IDs should not match", oldSessionId, newSessionId);
        assertEquals(3, map.size());
        assertEquals(1, map.get(newSessionId));
        assertEquals("first-value", map.get(newSessionId + HAZELCAST_SESSION_ATTRIBUTE_SEPARATOR + "first-key"));
        assertEquals("second-value", map.get(newSessionId + HAZELCAST_SESSION_ATTRIBUTE_SEPARATOR + "second-key"));

        assertNotEquals(oldSessionId, newSessionId);
    }

	@Ignore
    @Test
    public void testUpdateAndReadSameRequest() throws Exception {
        CookieStore cookieStore = new BasicCookieStore();
        assertEquals("true", executeRequest("write", serverPort1, cookieStore));
        assertEquals("value-updated", executeRequest("update-and-read-same-request", serverPort2, cookieStore));
    }

	@Ignore
    @Test
    public void testUpdateAndReadSameRequestWithRestart() throws Exception {
        CookieStore cookieStore = new BasicCookieStore();
        assertEquals("true", executeRequest("write", serverPort1, cookieStore));

        server1.restart();

        assertEquals("value-updated", executeRequest("update-and-read-same-request", serverPort1, cookieStore));
    }

	@Ignore
    @Test
    public void testIssue3132() throws Exception {
        CookieStore cookieStore = new BasicCookieStore();
        assertEquals("true", executeRequest("isNew", serverPort1, cookieStore));
        assertEquals("false", executeRequest("isNew", serverPort1, cookieStore));
        assertEquals("false", executeRequest("isNew", serverPort2, cookieStore));
        server1.restart();

        assertEquals("false", executeRequest("isNew", serverPort1, cookieStore));
        assertEquals("false", executeRequest("isNew", serverPort2, cookieStore));
    }



}
