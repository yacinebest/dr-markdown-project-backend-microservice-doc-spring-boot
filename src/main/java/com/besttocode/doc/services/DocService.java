package com.besttocode.doc.services;

import com.besttocode.doc.dtos.DocDTO;
import com.besttocode.doc.exceptions.UserNotAllowedException;

import java.util.List;

public interface DocService {
    void createDocument(DocDTO docDTO);

    List<DocDTO> fetchDocsForUserId(String userId, String callerUserId);

    DocDTO fetchDoc(String docId, String userId);

    List<DocDTO> fetchTopRecentDocs();

    void updateDoc(DocDTO docDTO, String userId) throws UserNotAllowedException;

    void deleteDocById(String docId);
}
