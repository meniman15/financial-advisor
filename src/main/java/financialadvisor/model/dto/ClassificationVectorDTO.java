package financialadvisor.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassificationVectorDTO {
  float openPrice;
  float closingPrice;
  float futurePrice;
  @JsonProperty("worthInvest")
  int worthInvest;

  public ClassificationVectorDTO(float openPrice, float closingPrice, float futurePrice,
      int worthInvest) {
    this.openPrice = openPrice;
    this.closingPrice = closingPrice;
    this.futurePrice = futurePrice;
    this.worthInvest = worthInvest;
  }

  public float getOpenPrice() {
    return openPrice;
  }

  public void setOpenPrice(float openPrice) {
    this.openPrice = openPrice;
  }

  public float getClosingPrice() {
    return closingPrice;
  }

  public void setClosingPrice(float closingPrice) {
    this.closingPrice = closingPrice;
  }

  public float getFuturePrice() {
    return futurePrice;
  }

  public void setFuturePrice(float futurePrice) {
    this.futurePrice = futurePrice;
  }

  public int isWorthInvest() {
    return worthInvest;
  }

  public void setWorthInvest(int worthInvest) {
    this.worthInvest = worthInvest;
  }
}
