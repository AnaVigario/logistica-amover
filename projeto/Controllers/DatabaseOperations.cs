using projeto.Data;
using projeto.Data.Models;
using System.Data.Entity;

namespace projeto.Controllers
{
    public class DatabaseOperations
    {
        public static BackEndContext Db=new BackEndContext();

        public void AdicionarUtilizador(string nome,string email,string senha, string tipo_utilizador)
        {
            Utilizadores novo = new Utilizadores();
            novo.tipo_utilizador = tipo_utilizador;
            novo.email = email;
            novo.name = nome;
            novo.senha = senha;
            Db.utilizadores.Add(novo);
            Db.SaveChanges();
        }

        public void EditarUtilizador(int id, string nome, string email, string senha, string tipo_utilizador)
        {
            Utilizadores user = Db.utilizadores.Where(x => x.id == id).FirstOrDefault();

            user.tipo_utilizador = tipo_utilizador;
            user.email = email;
            user.name = nome;
            user.senha = senha;
            Db.SaveChanges();
        }

        public void EliminarUtilizador(int id)
        {
            Utilizadores novo = Db.utilizadores.Where(x => x.id == id).FirstOrDefault();
            Db.utilizadores.Remove(novo);
            Db.SaveChanges();
        }

        public int Login(string email, string senha)
        {
            Utilizadores user = Db.utilizadores.Where(x => x.email == email).FirstOrDefault();
            
            if (user != null && user.senha == senha)
            {
                return user.id;
            }

            return -1;
        }

        public void AdicionarAlerta(string descricao, int id_admin, List<int> id_users)
        {
            AlertaManutencao alerta = new AlertaManutencao();
            alerta.descricao = descricao;
            alerta.data = DateTime.Now;
            alerta.admin = Db.utilizadores.Where(x => x.id == id_admin).FirstOrDefault();
            alerta.Utilizadores = new List<Utilizadores>();
            foreach (int id1 in id_users) 
            {
                alerta.Utilizadores.Add(Db.utilizadores.Where(x => x.id == id1).FirstOrDefault());
            }
            Db.alertas.Add(alerta);
            Db.SaveChanges();
        }

        public List<AlertaManutencao> VisualizarAlertas()
        {
            List<AlertaManutencao> alertas = Db.alertas.Include(y => y.Utilizadores).ToList();
            if (alertas != null)
            {
                
                return alertas;
            }
            return null;
        }

        public void AdicionarTarefa(string tipo, DateTime data, string descricao, string estado, int id, int servico, List<string> coordenadas)
        {
            Tarefa tarefa = new Tarefa();
            tarefa.tipo = tipo;
            tarefa.DeadLine = data;
            tarefa.data = DateTime.Now;
            tarefa.descricao= descricao;
            tarefa.estado = estado;
            tarefa.utilizador = Db.utilizadores.Where(x => x.id == id).FirstOrDefault();
            tarefa.servico = Db.servicos.Where(x => x.id_servico == servico).FirstOrDefault();
            tarefa.coordenadas = coordenadas;
            Db.tarefas.Add(tarefa);
            Db.SaveChanges();
        }

        public List<Tarefa> VisualizarTarefas()
        {
            return Db.tarefas.ToList();
        }

        public void AdicionarServico(string tipo, string descricao, string estado, int id)
        {
            Servico servico = new Servico();
            servico.tipo = tipo;
            servico.data = DateTime.Now;
            servico.descricao = descricao;
            servico.estado = estado;
            servico.utilizador = Db.utilizadores.Where(x => x.id == id).FirstOrDefault();
            Db.servicos.Add(servico);
            Db.SaveChanges();
        }

        public List<Servico> VisualizarServicos()
        {           
            return Db.servicos.ToList();
        }

        public void AdicionarRelatorioDesempenho(string descricao)
        {
            RelatorioDesempenho relatorioDesempenho = new RelatorioDesempenho();
            relatorioDesempenho.descricao = descricao;
            relatorioDesempenho.data = DateTime.Now;
            Db.relatorios.Add(relatorioDesempenho);
            Db.SaveChanges();
        }

        public List<RelatorioDesempenho> VisualizarRelatorioDesempenho()
        {
            return Db.relatorios.ToList();
        }
    }
}
