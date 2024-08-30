package com.daewon.xeno_backend.service;

import com.daewon.xeno_backend.domain.Products;
import com.daewon.xeno_backend.domain.auth.*;
import com.daewon.xeno_backend.dto.manager.PointUpdateDTO;
import com.daewon.xeno_backend.exception.BrandNotFoundException;
import com.daewon.xeno_backend.exception.UnauthorizedException;
import com.daewon.xeno_backend.exception.UserNotFoundException;
import com.daewon.xeno_backend.repository.Products.*;
import com.daewon.xeno_backend.repository.auth.BrandRepository;
import com.daewon.xeno_backend.repository.auth.CustomerRepository;
import com.daewon.xeno_backend.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
@Log4j2
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BrandRepository brandRepository;
    private final ProductsOptionRepository productsOptionRepository;
    private final ProductsImageRepository productsImageRepository;
    private final ProductsStarRepository productsStarRepository;
    private final ProductsLikeRepository productsLikeRepository;
    private final ProductsSellerRepository productsSellerRepository;
    private final ProductsRepository productsRepository;

    @Transactional
    @Override
    public String deleteUserByManager(String managerEmail, Long userIdToDelete) throws UserNotFoundException, UnauthorizedException {
        // Manager가 맞는지 검증 시도
        Users manager = validateManager(managerEmail);
        Users userToDelete = getUserById(userIdToDelete);

        log.info("현재 " + manager + "가 " + userToDelete + "user계정을 삭제합니다.");

        deleteUserData(userToDelete);

        log.info("UserId {} 가 manager {}의해서 회원 탈퇴가 완료되었습니다.", userIdToDelete, managerEmail);
        return managerEmail;
    }

    @Transactional
    @Override
    public void updateUserRoleByManager(String managerEmail, Long userId, Set<UserRole> newRoles) throws UserNotFoundException, UnauthorizedException {
        // Manager가 맞는지 검증
        Users manager = validateManager(managerEmail);
        Users user = getUserById(userId);

        log.info("Manager {} 가 해당 userId {} 의 role값을 변경중입니다.", managerEmail, userId);
        user.setRoleSet(newRoles);
        userRepository.save(user);
        log.info("userId {} 의 role값이 Manager {} 의하여 변경 됐습니다.", userId, managerEmail);
    }

    @Transactional
    @Override
    public void updateUserPointByManager(String managerEmail, Long userId, int newPoint) throws UserNotFoundException, UnauthorizedException {
        // Manager가 맞는지 검증
        Users manager = validateManager(managerEmail);
        Users user = getUserById(userId);

        log.info("Manager {} 가 userId {} 의 포인트를 {} 로 조정중입니다.", managerEmail, userId, newPoint);
        Customer customer = user.getCustomer();
        log.info("targetCustomer : " + customer);
        if (customer == null) {
            throw new UserNotFoundException("userId에 해당하는 Customer Data를 찾지 못했습니다. userId : " + userId);
        }

        // 현재 userId에 해당하는 point에서 + manager가 추가한 point값
        // 포인트 음수 방지
        int updatedPoint = Math.max(0, newPoint);
        customer.setPoint(updatedPoint);
        customerRepository.save(customer);
        log.info("userId {} 의 point가 {} 로 해당 {} manager에 의해 조정 되었습니다.", userId, updatedPoint, managerEmail);
    }

    @Transactional
    @Override
    public void updateUserLevelByManager(String managerEmail, Long userId, Level newLevel) throws UserNotFoundException, UnauthorizedException {
        // Manager가 맞는지 검증
        Users manager = validateManager(managerEmail);
        Users user = getUserById(userId);

        log.info("Manager {} 가 해당하는 userId {} 의 등급을 조정중입니다.", managerEmail, userId);
        Customer customer = user.getCustomer();
        if (customer == null) {
            throw new UserNotFoundException("userId에 해당하는 Customer Data를 찾지 못했습니다. userId : " + userId);
        }
        customer.setLevel(newLevel);
        customerRepository.save(customer);
        log.info("userId {} 의 등급이 해당 {} manager에 의해 조정 되었습니다.", userId, managerEmail);
    }

    // brand 강제 탈퇴
    // brand가 강제 탈퇴되면 해당 brand에 종속되어 있던 users도 함께 탈퇴됨.
    @Transactional
    @Override
    public String deleteBrandByManager(String managerEmail, Long brandIdToDelete, Long userToDelete) throws UserNotFoundException, UnauthorizedException {
        // Manager가 맞는지 검증
        Users manager = validateManager(managerEmail);

        Brand brandToDelete = getBrandById(brandIdToDelete);
        if (brandToDelete == null) {
            throw new BrandNotFoundException("해당 Brand를 찾을 수 없습니다: " + brandIdToDelete);
        }

        String brandName = brandToDelete.getBrandName();

        // Brand에 종속된 users 검색
        List<Users> usersToDelete = userRepository.findAllByBrand(brandToDelete);

        // user 삭제
        for (Users user : usersToDelete) {
            userRepository.delete(user);
        }

        // products 관련 데이터 삭제
        List<Products> products = productsRepository.findByBrandName(brandName);
        for (Products product : products) {
            deleteProductData(product);
        }

        // Brand 삭제
        brandRepository.delete(brandToDelete);

        log.info("현재 {} 가 {} 의 brand와 해당하는 brand의 user {} 계정을 삭제합니다.", manager, brandIdToDelete, userToDelete);

        return "Brand와 관련된 모든 사용자 계정이 성공적으로 삭제되었습니다.";
    }

    // product 강제 삭제
    @Override
    public void deleteProductByManager(String managerEmail, Long productIdToDelete) throws UserNotFoundException, UnauthorizedException {

    }

    // Manager인지 검증하는 메서드
    private Users validateManager(String managerEmail) throws UserNotFoundException, UnauthorizedException {
        Users manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new UserNotFoundException("Manager의 email을 찾을 수 없음 : " + managerEmail));

        if (manager.getManager() == null) {
            throw new UnauthorizedException("현재 사용자는 이 작업을 수행할 권한이 없습니다.");
        }
        return manager;
    }

    // user의 id를 가져오는 메서드
    private Users getUserById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User의 id를 찾을 수 없음: " + userId));
    }

    // userData를 삭제하는 메서드
    // 해당하는 user, customer값을 삭제함
    private void deleteUserData(Users user) {
        if (user.getCustomer() != null) {
            customerRepository.delete(user.getCustomer());
        }
        userRepository.delete(user);
    }

    // brand의 id를 가져오는 메서드
    private Brand getBrandById(Long brandId) throws BrandNotFoundException {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new BrandNotFoundException("Brand의 id를 찾을 수 없음: " + brandId));
    }

    // 현재 삭제하려는 product에 관련된 데이터를 지우는 메서드
    @Transactional
    protected void deleteProductData(Products product) {
        log.info("누구임? " + product.getProductId());
        int deletedOptions = productsOptionRepository.deleteAllByProductId(product.getProductId());
        log.info("제품 ID: {}에 대한 {} 개의 옵션이 삭제되었습니다.", product.getProductId(), deletedOptions);
        productsImageRepository.deleteByProducts(product);
        productsStarRepository.deleteByProducts(product);
        productsLikeRepository.deleteByProducts(product);
        productsSellerRepository.deleteByProducts(product);

        productsRepository.delete(product);

        log.info("제품 ID: {} 및 관련 데이터가 삭제되었습니다.", product.getProductId());

    }
}
