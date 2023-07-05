package com.example.blogram.domain.posts;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // Entity 클래스에서는 절대 Setter만들지 말기! -> 값 변경이 필요할 때는 명확히 그 목적과 의도를 나타낼 수 있는 메소드로 구현!
@NoArgsConstructor
@Entity
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 웬만하면 하기 -> 인덱스에 악영향 방지, 유니크한 조건 유지 등
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    // @Column을 하지 않아도 해당 클래스의 필드는 모두 칼럼이 된다. 사용하는 이유는 기본값 외에 추가로 변경이 필요한 옵션이 있으면 사용!
    private String author;

    @Builder
    public Posts(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
}
