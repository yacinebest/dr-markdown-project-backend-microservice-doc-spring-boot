package com.besttocode.doc.controllers;

import com.besttocode.doc.dtos.DocDTO;
import com.besttocode.doc.exceptions.UserNotAllowedException;
import com.besttocode.doc.services.DocService;
import com.besttocode.doc.services.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/doc")
public class DocController {


    @Autowired
    private DocService docService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public DocDTO createDocument(@RequestBody DocDTO docDTO) {

        docService.createDocument(docDTO);
        return docDTO;
    }


    @GetMapping("/{userId}/all")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<DocDTO> fetchUserDoc(@PathVariable String userId, HttpServletRequest request) {

        String jwtToken = getJwtToken(request);
        String callerUserId = tokenService.getUserId(jwtToken);

        return docService.fetchDocsForUserId(userId, callerUserId);
    }

    @GetMapping("/{docId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','ANONYMOUS')")
    public DocDTO fetchDocument(@PathVariable String docId, HttpServletRequest request) {

        String jwtToken = getJwtToken(request);
        String userId = tokenService.getUserId(jwtToken);

        return docService.fetchDoc(docId, userId);

    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('USER','ADMIN','ANONYMOUS')")
    public List<DocDTO> fetchRecentDocument() {

        return docService.fetchTopRecentDocs();

    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('USER','ADMIN','ANONYMOUS')")
    public DocDTO updateDoc(@RequestBody DocDTO docDTO, HttpServletRequest httpServletRequest) throws UserNotAllowedException {

        String jwtToken = getJwtToken(httpServletRequest);

        String userId = tokenService.getUserId(jwtToken);

        docService.updateDoc(docDTO, userId);

        return docDTO;
    }

    @DeleteMapping("/delete/{docId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void deleteDocById(@PathVariable String docId) {

        docService.deleteDocById(docId);

    }

    private String getJwtToken(HttpServletRequest httpServletRequest) {

        try {
            String token = httpServletRequest.getHeader(AUTHORIZATION);
            String jwtToken = StringUtils.removeStart(token, "Bearer ").trim();
            return jwtToken;
        } catch (NullPointerException e) {
            return StringUtils.EMPTY;
        }

    }
}
