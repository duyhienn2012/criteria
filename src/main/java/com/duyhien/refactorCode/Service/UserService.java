package com.duyhien.refactorCode.Service;

import com.duyhien.refactorCode.Dto.Request.UserCreationRequest;
import com.duyhien.refactorCode.Dto.Request.UserUpdateRequest;
import com.duyhien.refactorCode.Dto.Response.PageResponse;
import com.duyhien.refactorCode.Dto.Response.UserResponse;
import com.duyhien.refactorCode.Entity.User;
import com.duyhien.refactorCode.Exception.AppException;
import com.duyhien.refactorCode.Mapper.UserMapper;
import com.duyhien.refactorCode.Repository.SearchCriteria;
import com.duyhien.refactorCode.Repository.UserRepository;
import com.duyhien.refactorCode.Repository.UserSearchQueryCriteriaConsumer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public User createUser(UserCreationRequest request){
//        User user = new User();

        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException("exist user");

//        user.setUsername(request.getUsername());
//        user.setPassword(request.getPassword());
//        user.setFirstName(request.getFirstName());
//        user.setLastName(request.getLastName());
//        user.setDob(request.getDob());
        User user = userMapper.toUser(request);

        return userRepository.save(user);
    }


    public User updateUser(String userId, UserUpdateRequest request) {
        User user = getUser(userId);
//
//        user.setPassword(request.getPassword());
//        user.setFirstName(request.getFirstName());
//        user.setLastName(request.getLastName());
//        user.setDob(request.getDob());
        userMapper.updateUser(user, request);

        return userRepository.save(user);
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }

    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public User getUser(String id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

//    public PageResponse<?> advanceSearchWithCriteriaJoinTable(int pageNo, int pageSize, String sortBy, String address, String... search) {
//
//        List<SearchCriteria> criteriaList = new ArrayList<>();
//
//        if (search.length > 0) {
//            Pattern pattern = Pattern.compile(SEARCH_OPERATOR);
//            for (String s : search) {
//                Matcher matcher = pattern.matcher(s);
//                if (matcher.find()) {
//                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
//                }
//            }
//        }
//
//        if (StringUtils.hasLength(sortBy)) {
//            Pattern pattern = Pattern.compile(SORT_BY);
//            for (String s : search) {
//                Matcher matcher = pattern.matcher(s);
//                if (matcher.find()) {
//                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
//                }
//            }
//        }
//
//        List<User> users = getUsers(offset, pageSize, criteriaList, address, sortBy);
//
//        Long totalElements = getTotalElements(criteriaList);
//
//        Page<User> page = new PageImpl<>(users, PageRequest.of(offset, pageSize), totalElements);
//
//        return PageResponse.builder()
//                .pageNo(offset)
//                .pageSize(pageSize)
//                .totalPage(page.getTotalPages())
//                .items(users)
//                .build();
//    }

    public PageResponse advancSearchUser(int pageNo, int pageSize, String sortBy, String... search) {

        List<SearchCriteria> criteriaList = new ArrayList<>();

        for (String s: search) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(.*)");
            Matcher matcher = pattern.matcher(s);
            if (matcher.find())
                criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
        }

        List<User> users = getUsers(pageNo, pageSize, criteriaList, sortBy);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(0)
                .items(users)
                .build();
    }

    private List<User> getUsers(int offset, int pageSize, List<SearchCriteria> criteriaList, String sortBy) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchQueryCriteriaConsumer queryCriteriaConsumer = new UserSearchQueryCriteriaConsumer(criteriaBuilder, predicate, root);

        criteriaList.forEach(queryCriteriaConsumer);
        predicate  = queryCriteriaConsumer.getPredicate();

        query.where(predicate);
        return entityManager.createQuery(query).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }
}
