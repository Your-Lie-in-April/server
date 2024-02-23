package com.appcenter.timepiece.common.redis;


import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    RefreshToken findByMemberId(Long memberID);
}