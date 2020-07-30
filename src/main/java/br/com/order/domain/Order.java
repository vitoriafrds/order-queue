package br.com.order.domain;

import br.com.order.validator.Validator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Validator {

    private Long id;

    private String clientName;
    private Integer clientScore;

    private String cardOwner;
    private String cardNumber;

    private BigDecimal price;
    private Integer amount;

    public List<String> validationMessages = new ArrayList<>();

    public String getValidationMessagesAsJson() {
        StringBuilder json = new StringBuilder("messages:[");
        validationMessages.forEach(message -> {
            json.append("'" + message + "'" + ",");
        });
        json.append("]");
        return json.toString();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", clientName='" + clientName + '\'' +
                ", clientScore=" + clientScore +
                ", cardOwner='" + cardOwner + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                '}';
    }

    @Override
    public void validate() {
        if (!this.clientName.equals(cardOwner)) {
            validationMessages.add("Client name is not equal card owner");
        }

        if (this.amount > 500) {
            validationMessages.add("Amount too big for configured threshold");
        }

        if (this.clientScore < 50) {
            validationMessages.add("Score too low for configured threshold");
        }
    }

}
