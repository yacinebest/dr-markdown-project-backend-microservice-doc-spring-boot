package com.besttocode.doc.services.impl;

import com.besttocode.doc.daos.DocDAO;
import com.besttocode.doc.dtos.DocDTO;
import com.besttocode.doc.exceptions.DocDeleteException;
import com.besttocode.doc.exceptions.UserNotAllowedException;
import com.besttocode.doc.models.DocModel;
import com.besttocode.doc.services.DocService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.isNull;

@Service
@Slf4j
public class DocServiceImpl implements DocService {


    @Autowired
    private DocDAO docDAO;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void createDocument(DocDTO docDTO) {
        checkNotNull(docDTO.getContent());
        checkNotNull(docDTO.getTitle());
        checkNotNull(docDTO.getUserId());

        DocModel docModel = modelMapper.map(docDTO, DocModel.class);

        if (isNull(docModel.getAvailable())) {
            docModel.setAvailable(false);
        }
        docDAO.save(docModel);
        log.info("New doc has been created with id `" + docModel.getId() + "'");
        modelMapper.map(docModel, docDTO);
    }

    @Override
    public List<DocDTO> fetchDocsForUserId(String userId, String callerUserId) {

        List<DocModel> list = docDAO.findAllByUserIdOrderByUpdatedAtDesc(userId);

        log.info("Fetching all documents linked to user with id `" + userId + "'");

        if (userId.equals(callerUserId)) {
            return list.stream()
                    .filter(docModel -> docModel.getUserId().equals(callerUserId))
                    .map(docModel -> modelMapper.map(docModel, DocDTO.class))
                    .collect(Collectors.toList());

        } else {
            return list.stream()
                    .filter(DocModel::getAvailable)
                    .map(docModel -> modelMapper.map(docModel, DocDTO.class))
                    .collect(Collectors.toList());

        }
    }

    @Override
    public DocDTO fetchDoc(String docId, String userId) {

        Optional<DocModel> docById = docDAO.findById(docId);
        if (docById.isPresent()) {
            if (docById.get().getUserId().equals(userId)) {

                log.info("Retrieved doc with id `" + docId + "'");
                return modelMapper.map(docById, DocDTO.class);
            } else {
                if (docById.get().getAvailable()) {
                    return modelMapper.map(docById, DocDTO.class);
                }
            }
        }

        return null;
    }

    @Override
    public void deleteDocById(String docId) {

        Optional<DocModel> docModel = docDAO.findById(docId);

        if (docModel.isPresent()) {
            docDAO.deleteById(docId);
            log.info("Deleted doc with id `" + docId + "'");
        } else {
            throw new DocDeleteException("Theres no document with id '" + docId + "'");
        }

    }

    @Override
    public List<DocDTO> fetchTopRecentDocs() {

        Page<DocModel> recentlyUpdatedDocs = docDAO.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt")));

        return recentlyUpdatedDocs.stream()
                .map(doc -> modelMapper.map(doc, DocDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateDoc(DocDTO docDTO, String userId) throws UserNotAllowedException {

        Optional<DocModel> optionalDocModel = docDAO.findById(docDTO.getId());

        if (optionalDocModel.isPresent()) {

            DocModel docModel = optionalDocModel.get();
            if (docModel.getUserId().equals(userId)) {

                modelMapper.map(docDTO, docModel);

                docDAO.save(docModel);

                modelMapper.map(docModel, docDTO);
                return;
            } else {
                throw new UserNotAllowedException("You're not allowed to delete this document!");
            }

        }

        throw new NoSuchElementException("No document with id '" + docDTO.getId() + "' was found");
    }
}
