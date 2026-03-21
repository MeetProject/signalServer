package com.meetProject.signalserver.listener;

import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.service.message.TopicMessagingService;
import com.meetProject.signalserver.util.WebSocketUtils;
import java.time.Instant;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private final RoomsService roomsService;
    private final UserService userService;
    private final TopicMessagingService topicMessagingService;
    private final TaskScheduler taskScheduler;

    public WebSocketEventListener(RoomsService roomsService, UserService userService, TopicMessagingService topicMessagingService, TaskScheduler taskScheduler) {
        this.roomsService = roomsService;
        this.userService = userService;
        this.topicMessagingService = topicMessagingService;
        this.taskScheduler = taskScheduler;
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        taskScheduler.schedule(() -> {
            String userId = WebSocketUtils.getUserId(event.getUser());
            if (userId == null) {
                return;
            }

            String roomId = roomsService.getRoomId(userId);
            if(roomId != null) {
                roomsService.removeParticipant(roomId, userId);
                topicMessagingService.sendLeave(userId, roomId);
            }
            userService.removeUser(userId);
        }, Instant.now().plusSeconds(10));
    }
}
