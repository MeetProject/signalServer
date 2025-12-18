package com.meetProject.signalserver.model.socket.signal;

import com.meetProject.signalserver.model.User;
import java.util.List;

public record JoinResponse(String userId, String roomId, List<User> participants) {}
