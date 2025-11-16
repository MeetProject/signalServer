package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.dto.ErrorResponse;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class LeaveOrchestrationService {
    private final UserService userService;
    private final ScreenSharingService screenSharingService;
    private final SignalMessagingService signalMessagingService;
    private final RoomsService roomsService;

    public LeaveOrchestrationService(UserService userService, RoomsService roomsService, ScreenSharingService screenSharingService, SignalMessagingService signalMessagingService) {
        this.userService = userService;
        this.roomsService = roomsService;
        this.screenSharingService = screenSharingService;
        this.signalMessagingService = signalMessagingService;
    }

    public void leaveUser(String userId, StreamType streamType) {
        try {
            String roomId = userService.getRoomId(userId);
            if(roomId == null) {
                throw new IllegalArgumentException("Room Id is null");
            }

            if(streamType == StreamType.SCREEN) {
                stopScreenShare(userId, roomId);
            } else {
                leaveUser(userId, roomId);
            }
        } catch(Exception e){
            ErrorResponse response = new ErrorResponse(ErrorCode.E001, e.getMessage());
            signalMessagingService.sendError(userId, response);
        }
    }

    public void forceLeave(String userId, String roomId) {
        try {
            leaveUser(userId, roomId);
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void leaveUser(String userId, String roomId) {
        if(screenSharingService.isSharing(userId, roomId)) {
            stopScreenShare(userId, roomId);
        }

        roomsService.removeParticipant(roomId, userId);
        userService.updateRoomStatus(userId, null);
        signalMessagingService.sendLeave(roomId, userId, StreamType.USER);
    }

    private void stopScreenShare(String userId, String roomId) {
        if(!screenSharingService.isSharing(userId, roomId)) {
            throw new IllegalArgumentException("화면 공유자 상태가 아닙니다.");
        }

        screenSharingService.stopSharing(roomId);
        signalMessagingService.sendLeave(roomId, userId, StreamType.SCREEN);

    }
}
