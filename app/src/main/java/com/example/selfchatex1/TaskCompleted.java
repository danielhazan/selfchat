package com.example.selfchatex1;

public interface TaskCompleted{
    public void addToFirebase(Msg msg);
    public void loadFromFS();
}
