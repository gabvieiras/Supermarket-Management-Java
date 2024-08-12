public class Pagamento {
  private double valorTotal;
  private String forma;

  public Pagamento(double valorTotal, String forma) {
      this.valorTotal = valorTotal;
      this.forma = forma;
  }

  public double getValorTotal() {
      return valorTotal;
  }

  public String getForma() {
      return forma;
  }

  public void efetuarPagamento() {
      System.out.println("Pagamento efetuado no valor de R$ " + valorTotal + " via " + forma);
  }
}
