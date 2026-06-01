package com.batuscode.hosbes.utility

class BroadcastAction {
    enum class Type(val action: String) {
        SET_AUDIO_MUTED("com.recommyz.meet.SET_AUDIO_MUTED"),
        HANG_UP("com.recommyz.meet.HANG_UP"),
        SET_VIDEO_MUTED("com.recommyz.meet.SET_VIDEO_MUTED");
    }
}