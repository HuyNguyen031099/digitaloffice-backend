package com.datn.doffice.service;

import com.datn.doffice.dto.*;
import com.datn.doffice.entity.OfficialDispatchEntity;
import org.springframework.core.io.Resource;

import java.util.List;

public interface OfficialDispatchService {
    /**
     * Tạo công văn đến
     */
    void createComingDispatch(ComingDispatchDTO comingDispatchDTO, UserLoginDetailDTO userLoginDetailDTO);

    List<OfficialDispatchEntity> getAllComingDispatch();

    OfficialDispatchEntity getComingDispatchById(String comingDispatchId);

    OfficialDispatchEntity deleteComingDispatch(String comingDispatchId);

    /**
     * Chuyển tiếp (công văn đến)
     */
    void forward(ForwardOfficialDispatchDTO forwardOfficialDispatchDTO, UserLoginDetailDTO userLoginDetailDTO);

    /**
     * Phê duyệt (công văn đến)
     */
    void approve(String officialDispatchId, UserLoginDetailDTO userLoginDetailDTO);

    /**
     * Lấy số công văn
     */
    String getNumber(String officialDispatchId, UserLoginDetailDTO userLoginDetailDTO);

    /**
     * Update công văn
     */
    void updateDispatch(String dispatchId, ComingDispatchDTO comingDispatchDTO, UserLoginDetailDTO userLoginDetailDTO);

    List<Resource> getAllAttachmentByDispatch(String dispatchId);

    void addViewerToDispatch(AddUserViewDispatchDTO addUserViewDispatchDTO, UserLoginDetailDTO userLoginDetailDTO);

    /**
     * Quản lí công văn đi
     */
    List<OfficialDispatchEntity> getAllOutGoingDispatch();

    void createOutGoingDispatch(OutGoingDispatchDTO outGoingDispatchDTO, UserLoginDetailDTO userLoginDetailDTO);

    OfficialDispatchEntity getOutGoingDispatchById(String outGoingDispatchId);

    void submitToUnitLeadership(SubmitToUnitLeadershipDTO submitToUnitLeadershipDTO, UserLoginDetailDTO userLoginDetailDTO);
}
