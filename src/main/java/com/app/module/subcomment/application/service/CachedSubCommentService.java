package com.app.module.subcomment.application.service;

import com.app.module.comment.dto.request.UpdatedCommentRequest;
import com.app.module.subcomment.application.dto.CreateSubCommentRequest;
import com.app.module.subcomment.domain.SubComment;
import com.app.module.subcomment.domain.SubCommentNotFoundException;
import com.app.shared.helper.CachedServiceHelper;
import com.app.shared.type.SortType;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Primary
public class CachedSubCommentService extends CachedServiceHelper<SubComment>
    implements SubCommentService {

  private final SubCommentService subCommentService;

  public CachedSubCommentService(
      RedisTemplate<String, SubComment> subCommentRedisTemplate,
      @Qualifier("subCommentServiceImpl") SubCommentService subCommentService) {
    super(subCommentRedisTemplate);
    this.subCommentService = subCommentService;
  }

  @Override
  public SubComment save(CreateSubCommentRequest request) {
    var subComment = subCommentService.save(request);
    var cached =
        super.getOrLoad("subComment:" + subComment.getId(), () -> subComment, Duration.ofHours(2));
    return cached.orElse(subComment);
  }

  @Override
  public SubComment update(long id, UpdatedCommentRequest request) {
    var subComment = subCommentService.update(id, request);
    super.delete("subComment:" + subComment.getId());
    var cached =
        super.getOrLoad("subComment:" + subComment.getId(), () -> subComment, Duration.ofHours(2));
    return cached.orElse(subComment);
  }

  @Override
  public SubComment findById(long id) {
    var cached =
        super.getOrLoad(
            "subComment:" + id, () -> subCommentService.findById(id), Duration.ofHours(2));
    return cached.orElseThrow(
        () -> new SubCommentNotFoundException("Sub comment not found with a id: " + id));
  }

  @Override
  public List<SubComment> findAllByCommentId(
      long commentId, SortType sortType, int limit, int offset) {
    String redisKey = "comment:" + commentId + ":subComments:" + sortType;
    return super.getListOrLoad(
        redisKey,
        () -> subCommentService.findAllByCommentId(commentId, sortType, limit, offset),
        limit,
        offset,
        Duration.ofHours(2));
  }

  @Override
  public void deleteById(long id) {
    super.delete("subComment:" + id);
  }
}
