package bupt.usermicroservice.cotroller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private String baseUrl = "/User";

    private ObjectMapper objectMapper;

    @Before
    public void init() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private MvcResult post(String url, String content) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(url).content(content).contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    private MvcResult put(String url, String content) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.put(url).content(content).contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    private MvcResult delete(String url) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.delete(url).contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    private MvcResult get(String url) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

    }

    @Test
    public void loginSuccessful() throws Exception {
        String url = baseUrl + "/login";
        String contextJson = "{\n  \"name\": \"Facker\",\n  \"passwordToken\": \"123456789\"\n}";
        MvcResult result = post(url, contextJson);
        Assert.assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void loginErrorByName() throws Exception {
        String url = baseUrl + "/login";
        String contextJson = "{\n  \"name\": \"===Facker\",\n  \"passwordToken\": \"123456\"\n}";
        MvcResult result = post(url, contextJson);
        Assert.assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void loginErrorByPassword() throws Exception {
        String url = baseUrl + "/login";
        String contextJson = "{\n  \"name\": \"Facker\",\n  \"passwordToken\": \"18\"\n}";
        MvcResult result = post(url, contextJson);
        Assert.assertNotEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void register() throws Exception {
        String url = baseUrl + "/";
        //language=JSON
        String contextJson = "{\n" +
                "  \"username\": \"NewUser\",\n" +
                "  \"passwordToken\": \"123456789\",\n" +
                "  \"manager\": true,\n" +
                "  \"student\": false,\n" +
                "  \"teacher\": true\n" +
                "}";
        MvcResult result = put(url, contextJson);
        Assert.assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void delete() throws Exception {
        String url = baseUrl + "/";
        String username = "Facker";
        MvcResult result = delete(url + username);
        Assert.assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void update() throws Exception {
        String url = baseUrl + "/";
        String username = "Facker";
        //language=JSON
        String contextJson = "{\n" +
                "  \"username\": \"NewUser\",\n" +
                "  \"passwordToken\": \"123456789\",\n" +
                "  \"manager\": true,\n" +
                "  \"student\": false,\n" +
                "  \"teacher\": true\n" +
                "}";
        MvcResult result = post(url + username, contextJson);
        Assert.assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void get() throws Exception {
        String url = baseUrl + "/";
        String username = "Facker";
        MvcResult result = get(url + username);
        Assert.assertEquals(200, result.getResponse().getStatus());
        JsonNode user = objectMapper.readTree(result.getResponse().getContentAsString());
        Assert.assertEquals("Facker", user.get("username").asText());
        Assert.assertEquals(true, user.get("student").asBoolean());
    }

}
