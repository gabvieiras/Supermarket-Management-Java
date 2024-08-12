public class Produto {
  private String nome;
  private double preco;
  private int quantidadeEstoque;

  public Produto(String nome, double preco, int quantidadeEstoque) {
      this.nome = nome;
      this.preco = preco;
      this.quantidadeEstoque = quantidadeEstoque;
  }

  public String getNome() {
      return nome;
  }

  public double getPreco() {
      return preco;
  }

  public int getQuantidadeEstoque() {
      return quantidadeEstoque;
  }

  public void vender(int quantidade) {
      if (quantidade <= quantidadeEstoque) {
          quantidadeEstoque -= quantidade;
      } else {
          System.out.println("Estoque insuficiente para este produto.");
      }
  }
}
