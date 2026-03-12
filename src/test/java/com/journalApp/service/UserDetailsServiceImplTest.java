package com.journalApp.service;

import com.journalApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
    }


//    @Test
//    void loadUserByUsernameTest(){
//         when(userRepository.findByusername(ArgumentMatchers.anyString())).thenReturn((com.edigest.journalApp.entity.User) User.builder().username("Ram").password("TTVVsisg").build());
//        UserDetails user=userDetailsService.loadUserByUsername("Ram");
//        Assertions.assertNotNull(user);
//
//    }
    //Mockito does not required junit for testing
}
