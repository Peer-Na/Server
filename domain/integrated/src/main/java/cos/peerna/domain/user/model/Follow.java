package cos.peerna.domain.user.model;

import cos.peerna.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Follow extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn
    private User follower;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private User followee;

    public Follow(User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
        follower.getFollowings().add(this);
        followee.getFollowers().add(this);
    }
}
