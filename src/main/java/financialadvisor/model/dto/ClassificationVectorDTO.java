package financialadvisor.model.dto;

public class ClassificationVectorDTO {
  float openPrice;
  float closingPrice;
  float futurePrice;
  boolean worthInvest;

  public ClassificationVectorDTO(float openPrice, float closingPrice, float futurePrice,
      boolean worthInvest) {
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

  public boolean isWorthInvest() {
    return worthInvest;
  }

  public void setWorthInvest(boolean worthInvest) {
    this.worthInvest = worthInvest;
  }
}
