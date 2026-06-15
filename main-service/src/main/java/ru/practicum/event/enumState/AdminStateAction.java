package ru.practicum.event.enumState;

public enum AdminStateAction {
    PUBLISH_EVENT,  // СОБЫТИЕ_ОПУБЛИКОВАТЬ, обновит статус БД в PUBLISHED
    REJECT_EVENT    // СОБЫТИЕ_ОТКЛОНИТЬ, обновит статус БД в CANCELED
}
