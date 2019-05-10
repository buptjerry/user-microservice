package bupt.usermicroservice.cotroller;

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
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private String baseUrl = "/User";

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private MvcResult post(String url, String content) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(url).content(content).contentType(MediaType.APPLICATION_JSON))
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


}
