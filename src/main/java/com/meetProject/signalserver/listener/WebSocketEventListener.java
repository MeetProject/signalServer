package com.meetProject.signalserver.listener;

import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.MediaLeaveRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.LeaveResponse;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import com.meetProject.signalserver.service.RoomService;
import com.meetProject.signalserver.service.UserService;
import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private static final int GRACE_SECONDS = 10;

    private final RoomService roomService;
    private final UserService userService;
    private final StompMessageSender stompMessageSender;
    private final SimpUserRegistry userRegistry;
    private final TaskScheduler taskScheduler;
    private final Map<String, Object> pendingRemovals = new ConcurrentHashMap<>();

    public WebSocketEventListener(RoomService roomService, UserService userService, StompMessageSender stompMessageSender, SimpUserRegistry userRegistry, TaskScheduler taskScheduler) {
        this.roomService = roomService;
        this.userService = userService;
        this.stompMessageSender = stompMessageSender;
        this.userRegistry = userRegistry;
        this.taskScheduler = taskScheduler;
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal == null) {
            return;
        }
        String userId = principal.getName();

        Object ticket = new Object();
        pendingRemovals.put(userId, ticket);

        taskScheduler.schedule(() -> {
            if (!pendingRemovals.remove(userId, ticket)) {
                return;
            }
            if (userRegistry.getUser(userId) != null) {
                return;
            }

            try {
                roomService.leave(userId).ifPresent(roomId -> {
                    stompMessageSender.broadcast(roomId, TopicType.LEAVE, new LeaveResponse(userId));
                    stompMessageSender.sendToMediaServer(MediaType.LEAVE, new MediaLeaveRequest(roomId, userId));
                });
            } finally {
                userService.delete(userId);
            }
        }, Instant.now().plusSeconds(GRACE_SECONDS));
    }
}
