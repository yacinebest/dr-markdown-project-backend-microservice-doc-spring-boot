package com.besttocode.doc.services.impl;

import com.besttocode.doc.services.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class TokenServiceImpl implements TokenService {

    @Override
    public String getUserId(String jwtToken) {

        if (isEmpty(jwtToken)) {
            return StringUtils.EMPTY;
        }

        String claims = new String(Base64.getUrlDecoder().decode(jwtToken.split("\\.")[1]));
        JSONObject claimIsJson = new JSONObject(claims);


        return (String) claimIsJson.get("iss");
    }

    @Override
    public List<String> getUserRoles(String jwtToken) {

        String claims = new String(Base64.getUrlDecoder().decode(jwtToken.split("\\.")[1]));
        JSONObject claimIsJson = new JSONObject(claims);

        String audience = claimIsJson.getString("aud");

        String[] splits = audience
                .replace("[", "")
                .replace("]", "")
                .split(",");

        return Stream.of(splits).map(String::trim).collect(Collectors.toList());
    }
}
