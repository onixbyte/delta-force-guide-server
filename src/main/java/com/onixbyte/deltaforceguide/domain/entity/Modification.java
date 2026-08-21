package com.onixbyte.deltaforceguide.domain.entity;

import com.onixbyte.deltaforceguide.domain.converter.ModificationStatusConverter;
import com.onixbyte.deltaforceguide.enumeration.ModificationStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Entity representing a firearm modification or build configuration.
 *
 * @author zihluwang
 */
@Entity
@Table(
        name = "modification",
        indexes = {
                @Index(name = "idx_modification_firearm_id", columnList = "firearm_id")
        }
)
public class Modification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "firearm_id", nullable = false, foreignKey = @ForeignKey(name = "fk_modification_firearm"))
    private Firearm firearm;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Type(JsonType.class)
    @Column(name = "tags", columnDefinition = "jsonb", nullable = false)
    private List<String> tags = new ArrayList<>();

    @Type(JsonType.class)
    @Column(name = "accessories", columnDefinition = "jsonb", nullable = false)
    private List<Accessory> accessories = new ArrayList<>();

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "author", length = 64)
    private String author;

    @Column(name = "video_url", length = 512)
    private String videoUrl;

    @Column(name = "status", nullable = false)
    @Convert(converter = ModificationStatusConverter.class)
    private ModificationStatus status = ModificationStatus.DRAFT;

    @Column(name = "create_by")
    private Long createBy;

    @Column(name = "create_time", nullable = false)
    private OffsetDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Firearm getFirearm() {
        return firearm;
    }

    public void setFirearm(Firearm firearm) {
        this.firearm = firearm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Accessory> getAccessories() {
        return accessories;
    }

    public void setAccessories(List<Accessory> accessories) {
        this.accessories = accessories;
    }

    public void addAccessory(Accessory modificationAccessory) {
        this.accessories.add(modificationAccessory);
    }

    public void removeAccessory(Accessory modificationAccessory) {
        this.accessories.remove(modificationAccessory);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public OffsetDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(OffsetDateTime createTime) {
        this.createTime = createTime;
    }

    public ModificationStatus getStatus() {
        return status;
    }

    public void setStatus(ModificationStatus status) {
        this.status = status;
    }

    @PrePersist
    protected void prePersist() {
        this.createTime = OffsetDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private User user;
        private Firearm firearm;
        private String name;
        private String code;
        private List<String> tags;
        private List<Accessory> accessories;
        private String note;
        private String author;
        private String videoUrl;
        private Long createBy;
        private OffsetDateTime createTime;
        private ModificationStatus status;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder firearm(Firearm firearm) {
            this.firearm = firearm;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder accessories(List<Accessory> accessories) {
            this.accessories = accessories;
            return this;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder videoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            return this;
        }

        public Builder createBy(Long createBy) {
            this.createBy = createBy;
            return this;
        }

        public Builder createTime(OffsetDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder status(ModificationStatus status) {
            this.status = status;
            return this;
        }

        public Modification build() {
            Modification modification = new Modification();
            modification.id = this.id;
            modification.user = this.user;
            modification.firearm = this.firearm;
            modification.name = this.name;
            modification.code = this.code;
            modification.tags = Optional.ofNullable(this.tags).orElseGet(ArrayList::new);
            modification.accessories = Optional.ofNullable(this.accessories).orElseGet(ArrayList::new);
            modification.note = this.note;
            modification.author = this.author;
            modification.videoUrl = this.videoUrl;
            modification.createBy = this.createBy;
            modification.createTime = Optional.ofNullable(this.createTime).orElseGet(OffsetDateTime::now);
            modification.status = Optional.ofNullable(this.status).orElse(ModificationStatus.DRAFT);
            return modification;
        }
    }
}

