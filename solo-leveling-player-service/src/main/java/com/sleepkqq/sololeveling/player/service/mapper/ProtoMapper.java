package com.sleepkqq.sololeveling.player.service.mapper;

import static java.util.Objects.requireNonNull;

import com.sleepkqq.sololeveling.player.service.model.player.Level;
import com.sleepkqq.sololeveling.player.service.model.player.Player;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTask;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTaskTopic;
import com.sleepkqq.sololeveling.player.service.model.player.enums.Assessment;
import com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus;
import com.sleepkqq.sololeveling.player.service.model.task.Task;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskRarity;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskTopic;
import com.sleepkqq.sololeveling.player.service.model.user.User;
import com.sleepkqq.sololeveling.player.service.model.user.UserDraft;
import com.sleepkqq.sololeveling.player.service.model.user.enums.UserRole;
import com.sleepkqq.sololeveling.proto.player.LevelInfo;
import com.sleepkqq.sololeveling.proto.player.PlayerInfo;
import com.sleepkqq.sololeveling.proto.player.PlayerTaskInfo;
import com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicInfo;
import com.sleepkqq.sololeveling.proto.player.TaskInfo;
import com.sleepkqq.sololeveling.proto.user.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class ProtoMapper extends BaseMapper {

  public TaskTopic map(com.sleepkqq.sololeveling.proto.player.TaskTopic topic) {
    if (topic == null) {
      return null;
    }
    return TaskTopic.valueOf(topic.name());
  }

  public PlayerInfo map(Player player) {
    if (player == null) {
      return null;
    }
    return PlayerInfo.newBuilder()
        .setId(player.id())
        .setMaxTasks(player.maxTasks())
        .addAllPlayerTaskTopicInfo(mapCollection(player.taskTopics(), this::map))
        .build();
  }

  public LevelInfo map(Level level) {
    if (level == null) {
      return null;
    }
    return LevelInfo.newBuilder()
        .setId(map(level.id()))
        .setLevel(level.level())
        .setTotalExperience(level.totalExperience())
        .setCurrentExperience(level.currentExperience())
        .setExperienceToNextLevel(level.experienceToNextLevel())
        .setAssessment(map(level.assessment()))
        .build();
  }

  public com.sleepkqq.sololeveling.proto.player.Assessment map(Assessment assessment) {
    if (assessment == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.proto.player.Assessment.valueOf(assessment.name());
  }

  public PlayerTaskTopicInfo map(PlayerTaskTopic playerTaskTopic) {
    if (playerTaskTopic == null) {
      return null;
    }
    return PlayerTaskTopicInfo.newBuilder()
        .setId(map(playerTaskTopic.id()))
        .setTaskTopic(map(playerTaskTopic.taskTopic()))
        .setLevelInfo(map(requireNonNull(playerTaskTopic.level(), "level")))
        .build();
  }

  public com.sleepkqq.sololeveling.proto.player.TaskTopic map(TaskTopic taskTopic) {
    if (taskTopic == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.proto.player.TaskTopic.valueOf(taskTopic.name());
  }

  public PlayerTaskInfo map(PlayerTask playerTask) {
    if (playerTask == null) {
      return null;
    }
    var builder = PlayerTaskInfo.newBuilder()
        .setId(map(playerTask.id()))
        .setTaskInfo(map(playerTask.task()))
        .setStatus(map(playerTask.status()))
        .setCreatedAt(map(playerTask.createdAt()));
    set(map(playerTask.closedAt()), builder::setClosedAt);
    return builder.build();
  }

  public com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus map(
      PlayerTaskStatus playerTaskStatus
  ) {
    if (playerTaskStatus == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus.valueOf(playerTaskStatus.name());
  }

  public TaskInfo map(Task task) {
    if (task == null) {
      return null;
    }
    var builder = TaskInfo.newBuilder()
        .setId(map(task.id()))
        .addAllTopic(mapCollection(task.topics(), this::map));
    set(task.title(), builder::setTitle);
    set(task.description(), builder::setDescription);
    set(task.experience(), builder::setExperience);
    set(map(task.rarity()), builder::setRarity);
    set(task.agility(), builder::setAgility);
    set(task.strength(), builder::setStrength);
    set(task.intelligence(), builder::setIntelligence);
    return builder.build();
  }

  public com.sleepkqq.sololeveling.proto.player.TaskRarity map(TaskRarity taskRarity) {
    if (taskRarity == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.proto.player.TaskRarity.valueOf(taskRarity.name());
  }

  public UserInfo map(User user) {
    if (user == null) {
      return null;
    }
    return UserInfo.newBuilder()
        .setId(user.id())
        .setUsername(user.username())
        .setFirstName(user.firstName())
        .setLastName(user.lastName())
        .setPhotoUrl(user.photoUrl())
        .setLocale(user.locale())
        .addAllRole(mapCollection(user.roles(), this::map))
        .build();
  }

  public com.sleepkqq.sololeveling.proto.user.UserRole map(UserRole userRole) {
    if (userRole == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.proto.user.UserRole.valueOf(userRole.name());
  }

  public UserRole map(com.sleepkqq.sololeveling.proto.user.UserRole userRole) {
    if (userRole == null) {
      return null;
    }
    return UserRole.valueOf(userRole.name());
  }

  public User map(UserInfo userInfo) {
    if (userInfo == null) {
      return null;
    }
    return new UserDraft.Builder()
        .id(userInfo.getId())
        .username(userInfo.getUsername())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .photoUrl(userInfo.getPhotoUrl())
        .locale(userInfo.getLocale())
        .roles(mapCollection(userInfo.getRoleList(), this::map))
        .build();
  }
}
