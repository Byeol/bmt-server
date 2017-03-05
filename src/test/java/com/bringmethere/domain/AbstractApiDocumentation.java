package com.bringmethere.domain;

import com.bringmethere.user.domain.User;
import com.bringmethere.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureRestDocs("build/generated-snippets")
@AutoConfigureMockMvc
@ContextConfiguration
public abstract class AbstractApiDocumentation {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    ResultActions performGet(String url) throws Exception {
        return this.mockMvc.perform(get(url))
                .andExpect(status().isOk());
    }

    ResultActions performPost(String url, Object content) throws Exception {
        return this.mockMvc.perform(
                post(url)
                        .contentType(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(content)))
                .andExpect(status().isCreated());
    }

    ResultActions performPatch(String url, Object content) throws Exception {
        return this.mockMvc.perform(
                patch(url)
                        .contentType(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(content)))
                .andExpect(status().isNoContent());
    }

    String getLocation(String url, Object content) throws Exception {
        return performPost(url, content)
                .andReturn()
                .getResponse().getRedirectedUrl();
    }

    protected void initialize() {
        userRepository.deleteAll();

        User user = new User("admin");
        user.setName("관리자");
        user.setEmail("email@example.com");
        user.setAvatarUrl("http://example.com/avatar.jpg");
        userRepository.save(user);
    }
}