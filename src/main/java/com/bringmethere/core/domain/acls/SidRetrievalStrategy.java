package com.bringmethere.core.domain.acls;

import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface SidRetrievalStrategy {
    List<Sid> getSids(Authentication authentication);
}