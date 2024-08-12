import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {
    private static List<Produto> catalogoProdutos = new ArrayList<>();
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        exibirMenu();
    }

    public static void exibirMenu() throws IOException {
        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Cadastrar Produto");
            System.out.println("2. Realizar Venda");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");

            String opcao = reader.readLine().trim();

            switch (opcao) {
                case "1":
                    cadastrarProduto();
                    break;
                case "2":
                    realizarVenda();
                    break;
                case "3":
                    System.out.println("Saindo do sistema...");
                    reader.close();
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    public static void cadastrarProduto() throws IOException {
        System.out.println("\n=== Cadastro de Produto ===");

        System.out.print("Nome do Produto: ");
        String nome = reader.readLine();

        double preco = lerPrecoProduto();

        int quantidadeEstoque = lerQuantidadeEstoque();

        Produto produto = new Produto(nome, preco, quantidadeEstoque);
        catalogoProdutos.add(produto);

        System.out.println("Produto cadastrado com sucesso!");
    }

    public static double lerPrecoProduto() throws IOException {
        while (true) {
            try {
                System.out.print("Preço do Produto: ");
                String input = reader.readLine().trim();

                input = input.replace(',', '.');

                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Formato inválido. Digite o preço novamente.");
            }
        }
    }

    public static int lerQuantidadeEstoque() throws IOException {
        while (true) {
            try {
                System.out.print("Quantidade em Estoque: ");
                String input = reader.readLine().trim();

                if (!input.matches("\\d+")) {
                    throw new NumberFormatException("Formato inválido. Digite um número inteiro.");
                }

                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Formato inválido. Digite a quantidade novamente.");
            }
        }
    }

    public static void realizarVenda() throws IOException {
        if (catalogoProdutos.isEmpty()) {
            System.out.println("Não há produtos cadastrados. Cadastre produtos antes de realizar vendas.");
            return;
        }

        Cliente cliente = cadastrarCliente();
        Pedido pedido = new Pedido(cliente);

        while (true) {
            exibirCatalogo();
            Produto produtoEscolhido = escolherProduto();
            if (produtoEscolhido == null) {
                break;
            }

            adicionarItem(pedido, produtoEscolhido);

            System.out.print("\nDeseja adicionar mais produtos ao pedido? (s/n): ");
            String continuar;
            do {
                continuar = reader.readLine().trim().toLowerCase();
                if (!continuar.equals("s") && !continuar.equals("n")) {
                    System.out.println("Opção inválida. Digite 's' para sim ou 'n' para não.");
                }
            } while (!continuar.equals("s") && !continuar.equals("n"));

            if (continuar.equals("n")) {
                break;
            }
        }

        double valorTotal = calcularValorTotal(pedido);
        System.out.println("\nValor total do pedido: R$ " + formatarValor(valorTotal));

        String formaPagamento = selecionarFormaPagamento();

        Pagamento pagamento = new Pagamento(valorTotal, formaPagamento);
        pagamento.efetuarPagamento();

        atualizarEstoque(pedido);
    }

    public static Cliente cadastrarCliente() throws IOException {
        System.out.print("\nDigite o nome do cliente: ");
        String nomeCliente = reader.readLine();
        return new Cliente(nomeCliente);
    }

    public static Produto escolherProduto() throws IOException {
        System.out.print("\nEscolha o número do produto desejado (ou 's' para sair): ");
        String escolha = reader.readLine().trim().toLowerCase();

        if (escolha.equals("s")) {
            return null;
        }

        try {
            int indice = Integer.parseInt(escolha) - 1;
            if (indice >= 0 && indice < catalogoProdutos.size()) {
                return catalogoProdutos.get(indice);
            } else {
                System.out.println("Escolha inválida. Tente novamente.");
                return escolherProduto();
            }
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida. Tente novamente.");
            return escolherProduto();
        }
    }

    public static void adicionarItem(Pedido pedido, Produto produto) throws IOException {
        System.out.println("\nProduto selecionado: " + produto.getNome());
        System.out.println("Quantidade em estoque: " + produto.getQuantidadeEstoque());

        while (true) {
            try {
                System.out.print("Quantidade desejada: ");
                int quantidade = Integer.parseInt(reader.readLine());

                if (quantidade > produto.getQuantidadeEstoque()) {
                    System.out.println("Quantidade indisponível. Estoque atual: " + produto.getQuantidadeEstoque());
                } else {
                    ItemPedido item = new ItemPedido(produto, quantidade);
                    pedido.adicionarItem(item);
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Formato inválido. Digite a quantidade novamente.");
            }
        }
    }

    public static double calcularValorTotal(Pedido pedido) {
        double total = 0.0;
        for (ItemPedido item : pedido.getItens()) {
            total += item.getProduto().getPreco() * item.getQuantidade();
        }
        return total;
    }

    public static void atualizarEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.vender(item.getQuantidade());
        }
    }

    public static void exibirCatalogo() {
        System.out.println("\nCatálogo de Produtos Disponíveis:");
        for (int i = 0; i < catalogoProdutos.size(); i++) {
            Produto produto = catalogoProdutos.get(i);
            System.out.println((i + 1) + ". " + produto.getNome() + " - R$ " + formatarValor(produto.getPreco()) +
                    " - Quantidade disponível: " + produto.getQuantidadeEstoque());
        }
    }

    public static String formatarValor(double valor) {
        DecimalFormat df = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
        return df.format(valor);
    }

    public static String selecionarFormaPagamento() throws IOException {
        while (true) {
            System.out.println("\n=== Pagamento ===");
            System.out.println("Escolha a forma de pagamento:");
            System.out.println("1. Dinheiro");
            System.out.println("2. Cheque");
            System.out.println("3. Cartão");
            System.out.print("Opção: ");

            String formaPagamento = reader.readLine().trim();

            switch (formaPagamento) {
                case "1":
                    return "dinheiro";
                case "2":
                    return "cheque";
                case "3":
                    return "cartão";
                default:
                    System.out.println("Opção inválida. Escolha entre 1, 2 ou 3.");
            }
        }
    }
}
