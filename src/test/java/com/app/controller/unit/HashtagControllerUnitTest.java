package com.app.controller.unit;

import static org.junit.jupiter.api.Assertions.*;

import com.app.module.comment.application.service.CommentService;
import com.app.module.hashtag.api.HashtagController;
import com.app.module.pin.application.service.PinService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HashtagControllerUnitTest {
  @Mock private CommentService commentService;
  @Mock private PinService pinService;

  @InjectMocks private HashtagController hashtagController;
}
