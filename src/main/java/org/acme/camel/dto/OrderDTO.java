package org.acme.camel.dto;

public class OrderDTO {
    private Long id;

    public OrderDTO() {
    }

    public OrderDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                '}';
    }
}
