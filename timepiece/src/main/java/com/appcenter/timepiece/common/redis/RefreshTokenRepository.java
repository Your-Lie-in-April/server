package com.appcenter.timepiece.common.redis;


import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    RefreshToken findByMemberId(Long memberID);
}