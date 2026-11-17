package com.example.booking.model;

import com.example.booking.util.TextUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Абстрактное рабочее пространство, доступное для бронирования.
 *
 * <p>Наследники {@link MeetingRoom} и {@link CoworkingDesk} задают тип
 * пространства и его оснащение.</p>
 */
public abstract class Workspace implements Identifiable<Long> {

    private final Long id;
    private final String name;
    private final String location;
    private final int capacity;
    private BigDecimal pricePerHour;
    private boolean active;

    /**
     * Создаёт рабочее пространство.
     *
     * @param id           идентификатор
     * @param name         название
     * @param location     расположение
     * @param capacity     вместимость
     * @param pricePerHour цена за час
     * @param active       признак доступности
     */
    protected Workspace(Long id, String name, String location, int capacity,
                        BigDecimal pricePerHour, boolean active) {
        this.id = Objects.requireNonNull(id, "Идентификатор пространства не может быть null");
        if (id <= 0) {
            throw new IllegalArgumentException("Идентификатор пространства должен быть положительным");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Вместимость должна быть больше нуля");
        }
        this.name = TextUtils.requireText(name, "name");
        this.location = TextUtils.requireText(location, "location");
        this.capacity = capacity;
        this.pricePerHour = validatePrice(pricePerHour);
        this.active = active;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Возвращает тип пространства.
     *
     * @return тип
     */
    public abstract WorkspaceType getType();

    /**
     * Возвращает описание оснащения пространства.
     *
     * @return текстовое описание
     */
    public abstract String getEquipmentDescription();

    /**
     * Проверяет, подходит ли пространство для указанного количества людей.
     *
     * @param people количество людей
     * @return {@code true}, если вместимости достаточно
     */
    public boolean canAccommodate(int people) {
        return people > 0 && people <= capacity;
    }

    /**
     * Изменяет стоимость аренды за час.
     *
     * @param newPrice новая цена
     */
    public void changePrice(BigDecimal newPrice) {
        this.pricePerHour = validatePrice(newPrice);
    }

    /**
     * Деактивирует пространство: новые бронирования становятся недоступны.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Активирует пространство.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Возвращает название пространства с типом.
     *
     * @return подпись для вывода
     */
    public String getDisplayName() {
        return name + " [" + getType().getDisplayName() + "]";
    }

    private static BigDecimal validatePrice(BigDecimal price) {
        Objects.requireNonNull(price, "Цена не может быть null");
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Цена должна быть больше нуля");
        }
        return price.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Workspace workspace = (Workspace) other;
        return id.equals(workspace.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Workspace{id=" + id
                + ", name='" + name + '\''
                + ", type=" + getType()
                + ", location='" + location + '\''
                + ", capacity=" + capacity
                + ", pricePerHour=" + pricePerHour
                + ", active=" + active
                + '}';
    }
}
