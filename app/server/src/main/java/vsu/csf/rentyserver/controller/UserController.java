package vsu.csf.rentyserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vsu.csf.rentyserver.model.dto.auth.request.RegisterRequest;
import vsu.csf.rentyserver.model.dto.user.response.UserResponse;
import vsu.csf.rentyserver.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {
        userService.register(request);
    }

    @GetMapping("/{user_id}")
    public UserResponse findById(@PathVariable("user_id") Long userId) {
        return userService.findById(userId);
    }

    @GetMapping("/find")
    public UserResponse findById(@RequestParam("email") String email) {
        return userService.findByEmail(email);
    }

}
