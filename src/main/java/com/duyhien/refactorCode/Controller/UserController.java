package com.duyhien.refactorCode.Controller;

import com.duyhien.refactorCode.Dto.Request.UserCreationRequest;
import com.duyhien.refactorCode.Dto.Request.UserUpdateRequest;
import com.duyhien.refactorCode.Dto.Response.UserResponse;
import com.duyhien.refactorCode.Entity.User;
import com.duyhien.refactorCode.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    User createUser(@RequestBody UserCreationRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    List<UserResponse> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    User getUser(@PathVariable("userId") String userId){
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return "User has been deleted";
    }

    @GetMapping("/advance-search-with-criteria")
    public ResponseEntity advanceSearchWithCriteria(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                       @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                       @RequestParam(required = false) String sortBy,
                                                       @RequestParam(required = false) String address,
                                                       @RequestParam(defaultValue = "") String... search) {
        return ResponseEntity.ok(userService.advancSearchUser(pageNo, pageSize, sortBy,search));
    }
}
