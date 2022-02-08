package com.datn.doffice.service.impl;

import com.datn.doffice.defines.ActionOnDispatchTypes;
import com.datn.doffice.defines.OfficialDispatchStatus;
import com.datn.doffice.defines.UserViewTypes;
import com.datn.doffice.dto.*;
import com.datn.doffice.entity.*;
import com.datn.doffice.dao.*;
import com.datn.doffice.exceptions.*;
import com.datn.doffice.service.FileStorageService;
import com.datn.doffice.service.MailService;
import com.datn.doffice.service.OfficialDispatchService;
import com.datn.doffice.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
public class OfficialDispatchServiceImpl implements OfficialDispatchService {

    @Autowired
    private OfficialDispatchCollection officialDispatchCollection;

    @Autowired
    private UserViewTypeCollection userViewTypeCollection;

    @Autowired
    private UserCollection userCollection;

    @Autowired
    private AttachmentCollection attachmentCollection;

    @Autowired
    private UserViewDispatchCollection userViewDispatchCollection;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ActionOnDispatchCollection actionOnDispatchCollection;

    @Autowired
    private MailService mailService;

    @Autowired
    private ActivityHistoryCollection activityHistoryCollection;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createComingDispatch(ComingDispatchDTO comingDispatchDTO, UserLoginDetailDTO userLoginDetailDTO) {
        OfficialDispatchEntity officialDispatchEntity =
                OfficialDispatchEntity.builder()
                        .isComingDispatch(true)
                        .signBy(comingDispatchDTO.getSignBy())
                        .signDate(comingDispatchDTO.getSignDate())
                        .arrivalDate(comingDispatchDTO.getArrivalDate())
                        .documentTypeId(comingDispatchDTO.getDocumentTypeId())
                        .releaseDepartmentId(comingDispatchDTO.getReleaseDepartmentId())
                        .mainContent(comingDispatchDTO.getMainContent())
                        .totalPage(comingDispatchDTO.getTotalPage())
                        .securityLevel(comingDispatchDTO.getSecurityLevel())
                        .urgencyLevel(comingDispatchDTO.getUrgencyLevel())
                        .effectiveDate(comingDispatchDTO.getEffectiveDate())
                        .expirationDate(comingDispatchDTO.getExpirationDate())
                        .storageLocationId(comingDispatchDTO.getStorageLocationId())
                        .status(OfficialDispatchStatus.CHUA_XU_LY)  // 1 dang xu li | 2 da xu li
                        .createdAt(new Date())
                        .approveDate(null)
                        .isDeleted(false)
                        .createdBy(userLoginDetailDTO.getUserId())
                        .build();
        // insert OfficialDispatch
        officialDispatchCollection.insertObject(officialDispatchEntity);
        String officialDispatchId = officialDispatchEntity.getId();
        // Hiện tại chỉ làm 1 người nhận công văn.
        String receiverId = comingDispatchDTO.getReceiverId();
        // Type: processor
        UserViewTypeEntity processorType = userViewTypeCollection.findByCode(UserViewTypes.PROCESSER);
        UserViewTypeEntity viewerType = userViewTypeCollection.findByCode(UserViewTypes.VIEWER);
        // insert UserViewDispatchEntity
        UserEntity user = userCollection.findById(receiverId);
        // nguoi xu ly
        UserViewDispatchEntity processor = UserViewDispatchEntity.builder()
                .userId(user.getId())
                .officialDispatchId(officialDispatchId)
                .userViewTypeId(processorType.getId())
                .build();
        userViewDispatchCollection.insertObject(processor);
        // nguoi xem
        UserViewDispatchEntity viewer = UserViewDispatchEntity.builder()
                .userId(user.getId())
                .officialDispatchId(officialDispatchId)
                .userViewTypeId(viewerType.getId())
                .build();
        userViewDispatchCollection.insertObject(viewer);
        // insert AttachmentEntity
        List<MultipartFile> attachments = comingDispatchDTO.getAttachments();
        if (attachments == null) {
            throw new AttachmentInvalidException();
        }
        for (MultipartFile file : attachments) {
            String fileName = file.getOriginalFilename();
            long fileSize = file.getSize();
            String fileType = file.getContentType();
            String url = fileStorageService.storeFile(file);
            AttachmentEntity attachmentEntity = AttachmentEntity.builder()
                    .officialDispatchId(officialDispatchId)
                    .fileName(fileName)
                    .fileSize(fileSize)
                    .fileType(fileType)
                    .url(url)
                    .createdAt(new Date())
                    .isDeleted(false)
                    .build();
            attachmentCollection.insertObject(attachmentEntity);
        }
        // insert ActivityHistory
        ActionOnDispatchEntity action = actionOnDispatchCollection.findByActionCode(ActionOnDispatchTypes.CREATE);
        String actionId = action.getId();
        // create metadata
        Map<String, Object> metaData = new HashMap<>();
        // Nên chỉ lưu id thôi. Lúc hiển thị lên hệ thống thì phải truy vấn lại
        metaData.put("assignFor", user);
        ActivityHistoryEntity activityHistoryEntity = ActivityHistoryEntity.builder()
                .actionId(actionId)
                .userId(userLoginDetailDTO.getUserId())
                .officialDispatchId(officialDispatchId)
                .metaData(metaData)
                .createdAt(new Date())
                .isDeleted(false)
                .build();
        activityHistoryCollection.insertObject(activityHistoryEntity);
        // Send mail cho người nhận
//        mailService.notifyForReceiver(user);

    }

    @Override
    public List<OfficialDispatchEntity> getAllComingDispatch() {
        return officialDispatchCollection.getAllComingDispatch();
    }

    @Override
    public OfficialDispatchEntity getComingDispatchById(String comingDispatchId) {
        return officialDispatchCollection.findComingDispatchById(comingDispatchId);
    }

    @Override
    public OfficialDispatchEntity deleteComingDispatch(String comingDispatchId) {
        return officialDispatchCollection.deleteComingDispatch(comingDispatchId);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void forward(ForwardOfficialDispatchDTO forwardOfficialDispatchDTO, UserLoginDetailDTO userLoginDetailDTO) {
        String userId = forwardOfficialDispatchDTO.getUserId();
        if (CommonUtils.isNullOrEmpty(userId)) {
            throw new UserIdInvalidException();
        }
        // Nguoi tiep nhan
        UserEntity userEntity = userCollection.findById(userId);
        if (userEntity == null) {
            throw new UserNotFoundException();
        }
        String officialDispatchId = forwardOfficialDispatchDTO.getOfficialDispatchId();
        if (CommonUtils.isNullOrEmpty(officialDispatchId)) {
            throw new OfficialDispatchIdInvalidException();
        }
        // Cong van
        OfficialDispatchEntity officialDispatchEntity = officialDispatchCollection.findComingDispatchById(officialDispatchId);
        if (officialDispatchEntity == null) {
            throw new OfficialDispatchNotFoundException();
        }
        // insert vao bang UserViewDispatch
        UserViewTypeEntity userViewTypeEntity = userViewTypeCollection.findByCode(UserViewTypes.PROCESSER);
        UserViewDispatchEntity check = userViewDispatchCollection.findByUserIdAndDispatchId(userId, officialDispatchId);
        if (check == null) { // insert
            UserViewDispatchEntity userViewDispatchEntity = UserViewDispatchEntity.builder()
                    .userId(userEntity.getId())
                    .officialDispatchId(officialDispatchEntity.getId())
                    .userViewTypeId(userViewTypeEntity.getId())
                    .isDeleted(false)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .build();
            userViewDispatchCollection.insertObject(userViewDispatchEntity);
        } else { // update
            check.setUserViewTypeId(userViewTypeEntity.getId());
            check.setUpdatedAt(new Date());
            userViewDispatchCollection.updateObject(check);
        }
        // insert activity_history
        ActionOnDispatchEntity action = actionOnDispatchCollection.findByActionCode(ActionOnDispatchTypes.FORWARD);
        String actionId = action.getId();
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("Nội dung chuyển tiếp", forwardOfficialDispatchDTO.getContent());
        ActivityHistoryEntity activityHistoryEntity = ActivityHistoryEntity.builder()
                .actionId(actionId)  // hanh dong chuyen tiep
                .userId(userLoginDetailDTO.getUserId())
                .officialDispatchId(officialDispatchId)
                .isDeleted(false)
                .createdAt(new Date())
                .metaData(metaData)
                .build();
        activityHistoryCollection.insertObject(activityHistoryEntity);
        // Gui mail cho nguoi tiep nhan
//        mailService.notifyForReceiver(userEntity);
    }

    @Override
    public void approve(String officialDispatchId, UserLoginDetailDTO userLoginDetailDTO) {
        if (CommonUtils.isNullOrEmpty(officialDispatchId)) {
            throw new OfficialDispatchIdInvalidException();
        }
        OfficialDispatchEntity officialDispatchEntity = officialDispatchCollection.findComingDispatchById(officialDispatchId);
        if (officialDispatchEntity == null) {
            throw new OfficialDispatchNotFoundException();
        }
        officialDispatchEntity.setStatus(OfficialDispatchStatus.DA_XU_LY);
        officialDispatchEntity.setApproveDate(new Date());
        officialDispatchCollection.updateObject(officialDispatchEntity);
        // insert activity_history
        ActionOnDispatchEntity action = actionOnDispatchCollection.findByActionCode(ActionOnDispatchTypes.APPROVE);
        String actionId = action.getId();
        Map<String, Object> metaData = new HashMap<>();
        ActivityHistoryEntity activityHistoryEntity = ActivityHistoryEntity.builder()
                .officialDispatchId(officialDispatchId)
                .userId(userLoginDetailDTO.getUserId())
                .actionId(actionId)
                .metaData(metaData)
                .isDeleted(false)
                .createdAt(new Date())
                .build();
        activityHistoryCollection.insertObject(activityHistoryEntity);
    }

    @Override
    public String getNumber(String officialDispatchId, UserLoginDetailDTO userLoginDetailDTO) {
        if (CommonUtils.isNullOrEmpty(officialDispatchId)) {
            throw new OfficialDispatchIdInvalidException();
        }
        OfficialDispatchEntity officialDispatchEntity = officialDispatchCollection.findComingDispatchById(officialDispatchId);
        if (officialDispatchEntity == null) {
            throw new OfficialDispatchNotFoundException();
        }
        Integer status = officialDispatchEntity.getStatus();
        if (!OfficialDispatchStatus.DA_XU_LY.equals(status)) {
            throw new OfficialDispatchIsNotApprovedException();
        }
        // thuc hien lay so van ban
        return null;
    }

    @Override
    public void updateDispatch(String dispatchId, ComingDispatchDTO comingDispatchDTO, UserLoginDetailDTO userLoginDetailDTO) {
        if (CommonUtils.isNullOrEmpty(dispatchId)) {
            throw new OfficialDispatchIdInvalidException();
        }
        OfficialDispatchEntity officialDispatchEntity = officialDispatchCollection.findComingDispatchById(dispatchId);
        if (officialDispatchEntity == null) {
            throw new OfficialDispatchNotFoundException();
        }
        Boolean isValid = validateDispatchDTO(comingDispatchDTO);
        if (isValid) {
            officialDispatchEntity.setDocumentNumber(comingDispatchDTO.getDocumentNumber());
            officialDispatchEntity.setReleaseDepartmentId(comingDispatchDTO.getReleaseDepartmentId());
            officialDispatchEntity.setSignBy(comingDispatchDTO.getSignBy());
            officialDispatchEntity.setSignDate(comingDispatchDTO.getSignDate());
            officialDispatchEntity.setArrivalDate(comingDispatchDTO.getArrivalDate());
            officialDispatchEntity.setDocumentTypeId(comingDispatchDTO.getDocumentTypeId());
            officialDispatchEntity.setMainContent(comingDispatchDTO.getMainContent());
            officialDispatchEntity.setTotalPage(comingDispatchDTO.getTotalPage());
            officialDispatchEntity.setSecurityLevel(comingDispatchDTO.getSecurityLevel());
            officialDispatchEntity.setUrgencyLevel(comingDispatchDTO.getUrgencyLevel());
            officialDispatchEntity.setEffectiveDate(comingDispatchDTO.getEffectiveDate());
            officialDispatchEntity.setExpirationDate(comingDispatchDTO.getExpirationDate());
            officialDispatchEntity.setStorageLocationId(comingDispatchDTO.getStorageLocationId());
            officialDispatchEntity.setUpdatedAt(new Date());
            officialDispatchEntity.setUpdatedBy(userLoginDetailDTO.getUserId());
            officialDispatchCollection.updateObject(officialDispatchEntity);
            //update attachment
            // Xoa het attachment cu di
            List<AttachmentEntity> attachments = attachmentCollection.findAllAttachmentByDispatchId(dispatchId);
            if (attachments != null && !attachments.isEmpty()) {
                for (AttachmentEntity item : attachments) {
                    // Xoa trong server
                    fileStorageService.deleteFile(item.getFileName());
                    // Xoa trong database
                    item.setIsDeleted(true);
                    attachmentCollection.updateObject(item);
                }
            }
            // insert attachment moi vao
            List<MultipartFile> newAttachments = comingDispatchDTO.getAttachments();
            if (newAttachments != null && !newAttachments.isEmpty()) {
                for (MultipartFile file : newAttachments) {
                    String fileName = file.getOriginalFilename();
                    long fileSize = file.getSize();
                    String fileType = file.getContentType();
                    String url = fileStorageService.storeFile(file);
                    AttachmentEntity attachmentEntity = AttachmentEntity.builder()
                            .officialDispatchId(dispatchId)
                            .fileName(fileName)
                            .fileSize(fileSize)
                            .fileType(fileType)
                            .url(url)
                            .createdAt(new Date())
                            .isDeleted(false)
                            .build();
                    attachmentCollection.insertObject(attachmentEntity);
                }
            }
            // insert activity_history
            ActionOnDispatchEntity action = actionOnDispatchCollection.findByActionCode(ActionOnDispatchTypes.EDIT);
            String actionId = action.getId();
            ActivityHistoryEntity activityHistoryEntity = ActivityHistoryEntity.builder()
                    .actionId(actionId)
                    .userId(userLoginDetailDTO.getUserId())
                    .officialDispatchId(dispatchId)
                    .metaData(null)
                    .createdAt(new Date())
                    .isDeleted(false)
                    .build();
            activityHistoryCollection.insertObject(activityHistoryEntity);
        } else {
            return;
        }
    }

    private Boolean validateDispatchDTO(ComingDispatchDTO comingDispatchDTO) {
        Boolean isValid = true;
        if (CommonUtils.isNullOrEmpty(comingDispatchDTO.getDocumentNumber())) {
            isValid = false;
        }
        if (CommonUtils.isNullOrEmpty(comingDispatchDTO.getReleaseDepartmentId())) {
            isValid = false;
        }
        if (CommonUtils.isNullOrEmpty(comingDispatchDTO.getSignBy())) {
            isValid = false;
        }
        if (comingDispatchDTO.getSignDate() == null) {
            isValid = false;
        }
        if (comingDispatchDTO.getArrivalDate() == null) {
            isValid = false;
        }
        if (CommonUtils.isNullOrEmpty(comingDispatchDTO.getDocumentTypeId())) {
            isValid = false;
        }
        if (CommonUtils.isNullOrEmpty(comingDispatchDTO.getMainContent())) {
            isValid = false;
        }
        if (comingDispatchDTO.getTotalPage() == null) {
            isValid = false;
        }
        if (comingDispatchDTO.getSecurityLevel() == null) {
            isValid = false;
        }
        if (comingDispatchDTO.getUrgencyLevel() == null) {
            isValid = false;
        }
        if (comingDispatchDTO.getEffectiveDate() == null) {
            isValid = false;
        }
        if (comingDispatchDTO.getEffectiveDate() == null) {
            isValid = false;
        }
        if (comingDispatchDTO.getExpirationDate() == null) {
            isValid = false;
        }
        if (CommonUtils.isNullOrEmpty(comingDispatchDTO.getStorageLocationId())) {
            isValid = false;
        }
        if (CommonUtils.isNullOrEmpty(comingDispatchDTO.getReceiverId())) {
            isValid = false;
        }
        if (comingDispatchDTO.getAttachments() == null || comingDispatchDTO.getAttachments().isEmpty()) {
            isValid = false;
        }
        return isValid;
    }

    @Override
    public List<Resource> getAllAttachmentByDispatch(String dispatchId) {
        if (CommonUtils.isNullOrEmpty(dispatchId)) {
            throw new OfficialDispatchIdInvalidException();
        }
        List<AttachmentEntity> attachments = attachmentCollection.findAllAttachmentByDispatchId(dispatchId);
        List<Resource> resources = new ArrayList<>();
        for (AttachmentEntity item : attachments) {
            Resource resource = fileStorageService.loadFileAsResource(item.getFileName());
            resources.add(resource);
        }
        return resources;
    }

    @Override
    public void addViewerToDispatch(AddUserViewDispatchDTO addUserViewDispatchDTO, UserLoginDetailDTO userLoginDetailDTO) {
        String userId = addUserViewDispatchDTO.getUserId();
        if (CommonUtils.isNullOrEmpty(userId)) {
            throw new UserIdInvalidException();
        }
        UserEntity check = userCollection.findById(userId);
        if (check == null) {
            throw new UserNotFoundException();
        }
        String officialDispatchId = addUserViewDispatchDTO.getOfficialDispatchId();
        if (CommonUtils.isNullOrEmpty(officialDispatchId)) {
            throw new OfficialDispatchIdInvalidException();
        }
        // Hien tai dang lam cho van ban den
        OfficialDispatchEntity officialDispatchEntity = officialDispatchCollection.findComingDispatchById(officialDispatchId);
        if (officialDispatchEntity == null) {
            throw new OfficialDispatchNotFoundException();
        }
        UserViewTypeEntity viewerType = userViewTypeCollection.findByCode(UserViewTypes.VIEWER);
        UserViewDispatchEntity checkExist = userViewDispatchCollection.findByUserIdAndDispatchIdAndUserViewTypeId(userId, officialDispatchId, viewerType.getId());
        if (checkExist != null) { // neu ton tai
            throw new UserViewDispatchConflictException();
        }
        UserViewDispatchEntity userViewDispatchEntity = UserViewDispatchEntity.builder()
                .userId(userId)
                .officialDispatchId(officialDispatchId)
                .userViewTypeId(viewerType.getId())
                .isDeleted(false)
                .createdAt(new Date())
                .build();
        userViewDispatchCollection.insertObject(userViewDispatchEntity);

        // insert activity_history
        ActionOnDispatchEntity action = actionOnDispatchCollection.findByActionCode(ActionOnDispatchTypes.ADD_VIEWER);
        String actionId = action.getId();
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("addViewer", check);
        ActivityHistoryEntity activityHistoryEntity = ActivityHistoryEntity.builder()
                .actionId(actionId)
                .userId(userLoginDetailDTO.getUserId())
                .officialDispatchId(officialDispatchId)
                .metaData(metaData)
                .isDeleted(false)
                .createdAt(new Date())
                .build();
        activityHistoryCollection.insertObject(activityHistoryEntity);
    }

    /**
     *  Quản lý công văn đi
     */

    @Override
    public List<OfficialDispatchEntity> getAllOutGoingDispatch() {
        return officialDispatchCollection.getAllOutGoingDispatch();
    }

    @Override
    public OfficialDispatchEntity getOutGoingDispatchById(String outGoingDispatchId) {
        return officialDispatchCollection.findOutGoingDispatchById(outGoingDispatchId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createOutGoingDispatch(OutGoingDispatchDTO outGoingDispatchDTO, UserLoginDetailDTO userLoginDetailDTO) {
        OfficialDispatchEntity officialDispatchEntity = OfficialDispatchEntity.builder()
                .isComingDispatch(false)
                .receiveAddress(outGoingDispatchDTO.getReceiveAddress())
                .releaseDepartmentId(outGoingDispatchDTO.getReleaseDepartmentId())
                .documentTypeId(outGoingDispatchDTO.getDocumentTypeId())
                .mainContent(outGoingDispatchDTO.getMainContent())
                .totalPage(outGoingDispatchDTO.getTotalPage())
                .securityLevel(outGoingDispatchDTO.getSecurityLevel())
                .urgencyLevel(outGoingDispatchDTO.getUrgencyLevel())
                .effectiveDate(outGoingDispatchDTO.getEffectiveDate())
                .expirationDate(outGoingDispatchDTO.getExpirationDate())
                .status(OfficialDispatchStatus.CHUA_XU_LY)  // 1 dang xu li | 2 da xu li
                .createdAt(new Date())
                .approveDate(null)
                .isDeleted(false)
                .createdBy(userLoginDetailDTO.getUserId())
                .build();
        officialDispatchCollection.insertObject(officialDispatchEntity);
        String officialDispatchId = officialDispatchEntity.getId();
        // insert Attachments
        List<MultipartFile> attachments = outGoingDispatchDTO.getAttachments();
        if (attachments == null) {
            throw new AttachmentInvalidException();
        }
        for (MultipartFile file : attachments) {
            String fileName = file.getOriginalFilename();
            long fileSize = file.getSize();
            String fileType = file.getContentType();
            String url = fileStorageService.storeFile(file);
            AttachmentEntity attachmentEntity = AttachmentEntity.builder()
                    .officialDispatchId(officialDispatchId)
                    .fileName(fileName)
                    .fileSize(fileSize)
                    .fileType(fileType)
                    .url(url)
                    .createdAt(new Date())
                    .isDeleted(false)
                    .build();
            attachmentCollection.insertObject(attachmentEntity);
        }
        // insert activity_history
        ActionOnDispatchEntity action = actionOnDispatchCollection.findByActionCode(ActionOnDispatchTypes.CREATE);
        String actionId = action.getId();
        // create metadata
        Map<String, Object> metaData = new HashMap<>();
        ActivityHistoryEntity activityHistoryEntity = ActivityHistoryEntity.builder()
                .actionId(actionId)
                .userId(userLoginDetailDTO.getUserId())
                .officialDispatchId(officialDispatchId)
                .metaData(metaData)
                .createdAt(new Date())
                .isDeleted(false)
                .build();
        activityHistoryCollection.insertObject(activityHistoryEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submitToUnitLeadership(SubmitToUnitLeadershipDTO submitToUnitLeadershipDTO, UserLoginDetailDTO userLoginDetailDTO) {
        String outGoingDispatchId = submitToUnitLeadershipDTO.getOfficialDispatchId();
        String unitLeadershipId = submitToUnitLeadershipDTO.getUserId();
        if (CommonUtils.isNullOrEmpty(outGoingDispatchId)) {
            throw new OfficialDispatchIdInvalidException();
        }
        OfficialDispatchEntity officialDispatchEntity = officialDispatchCollection.findOutGoingDispatchById(outGoingDispatchId);
        if (officialDispatchEntity == null) {
            throw new OfficialDispatchNotFoundException();
        }
        if (CommonUtils.isNullOrEmpty(unitLeadershipId)) {
            throw new UserIdInvalidException();
        }
        UserEntity unitLeadership = userCollection.findById(unitLeadershipId);
        if (unitLeadership == null) {
            throw new UserNotFoundException();
        }
        officialDispatchEntity.setStatus(OfficialDispatchStatus.TRINH_LANH_DAO_DON_VI_KY);
        officialDispatchCollection.updateObject(officialDispatchEntity);
        // insert activity_history
        ActionOnDispatchEntity action = actionOnDispatchCollection.findByActionCode(ActionOnDispatchTypes.TRINH_LANH_DAO_DON_VI);
        String actionId = action.getId();
        // create metadata
        Map<String, Object> metaData = new HashMap<String, Object>() {{
            put("Trình lãnh đạo đơn vị", unitLeadership);
        }};
        ActivityHistoryEntity activityHistoryEntity = ActivityHistoryEntity.builder()
                .actionId(actionId)
                .userId(userLoginDetailDTO.getUserId())
                .officialDispatchId(outGoingDispatchId)
                .metaData(metaData)
                .createdAt(new Date())
                .isDeleted(false)
                .build();
        activityHistoryCollection.insertObject(activityHistoryEntity);
        // insert user_view_dispatch
        UserViewTypeEntity processorType = userViewTypeCollection.findByCode(UserViewTypes.PROCESSER);
        UserViewDispatchEntity processor = UserViewDispatchEntity.builder()
                .userId(unitLeadershipId)
                .officialDispatchId(outGoingDispatchId)
                .userViewTypeId(processorType.getId())
                .build();
        userViewDispatchCollection.insertObject(processor);
        // send mail
//        mailService.notifyForReceiver(unitLeadership);
    }
}
