package com.surest.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.api.dto.MemberDTO;
import com.surest.api.model.Member;
import com.surest.api.model.Role;
import com.surest.api.model.User;
import org.junit.jupiter.api.Tag;
import com.surest.api.repository.MemberRepository;
import com.surest.api.repository.RoleRepository;
import com.surest.api.repository.UserRepository;
import com.surest.api.config.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for MemberController.
 *
 * Important notes:
 *  - Uses profile "test" so application-test.properties (app.jwt.secret) is used.
 *  - Creates roles named "ADMIN" and "USER" (these strings match your SecurityConfig hasAuthority/hasAnyAuthority checks).
 *  - Generates JWTs using JwtTokenUtil.generateAccessToken(user) after saving the user (so user.id exists).
 *  - Date format for JSON payloads is yyyy-MM-dd (LocalDate).
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Tag("integration")
class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        memberRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role userRole = roleRepository.save(Role.builder().name("USER").build());

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles(Set.of(adminRole))
                .build();
        admin = userRepository.save(admin);


        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .roles(Set.of(userRole))
                .build();
        user = userRepository.save(user);


        adminToken = "Bearer " + jwtTokenUtil.generateAccessToken(admin);
        userToken = "Bearer " + jwtTokenUtil.generateAccessToken(user);


        memberRepository.save(Member.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build());
    }

    @Test
    void adminCanCreateMember() throws Exception {
        String json = """
                {
                    "firstName": "Alice",
                    "lastName": "Smith",
                    "email": "alice@example.com",
                    "dateOfBirth": "1995-10-05"
                }
                """;

        mockMvc.perform(post("/api/v1/members")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void userCannotCreateMember() throws Exception {
        String json = """
                {
                    "firstName": "Bob",
                    "lastName": "Brown",
                    "email": "bob@example.com",
                    "dateOfBirth": "1992-03-14"
                }
                """;

        mockMvc.perform(post("/api/v1/members")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void userCanGetMembers() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].firstName").value("John"))
        ;
    }

    @Test
    void adminCanUpdateMember() throws Exception {
        // fetch one member id
        Member existing = memberRepository.findAll().stream().findFirst().orElseThrow();
        MemberDTO dto = new MemberDTO();
        dto.setFirstName("JohnUpdated");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setDateOfBirth(LocalDate.of(1990,1,1));
        mockMvc.perform(put("/api/v1/members/" + existing.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("JohnUpdated"));
    }

    @Test
    void adminCanDeleteMember() throws Exception {
        Member existing = memberRepository.findAll().stream().findFirst().orElseThrow();
        mockMvc.perform(delete("/api/v1/members/" + existing.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void getMembersFilteredByFirstName() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                        .param("firstName", "Jo")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].firstName").value("John"));
    }

    @Test
    void getMembersFilteredByLastName() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                        .param("lastName", "D")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].lastName").value("Doe"));
    }
}
