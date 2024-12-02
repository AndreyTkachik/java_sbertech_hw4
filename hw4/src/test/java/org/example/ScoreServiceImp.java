package org.example;

public class ScoreServiceImp implements ScoreService {
    @Override
    public double score(Person person) {
        return person.getAge() * 2;
    }

    @Override
    public double nextAge(Person person) {
        return person.getAge() + 1;
    }
}
