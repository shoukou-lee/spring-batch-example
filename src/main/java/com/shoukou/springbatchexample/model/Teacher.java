package com.shoukou.springbatchexample.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String subject;

    /**
     * @AllArgsConstructor 와 @Builder 를 같이 쓸 경우, List<T> 초기화가 안되는 문제
     * https://choibulldog.tistory.com/43
     *
     * NOTE : 더 방어적인 코딩을 하려면, 따로 만든 생성자에 @Builder 를 적용한다.
     */
    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Student> students = new ArrayList<>();

    public void addStudent(Student student) {
        students.add(student);
        student.setTeacher(this);
    }

}
