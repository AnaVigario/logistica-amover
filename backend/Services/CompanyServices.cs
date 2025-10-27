using projeto.Data;
using projeto.Data.Models;

namespace projeto.Services
{
    public class CompanyServices
    {
        private readonly AMoverContext _context;
        public CompanyServices(AMoverContext context)
        {
            _context = context;
        }

        public void CreateCompany(Company company)
        {
            try
            {
                if (company == null)
                {
                    throw new ArgumentNullException(nameof(company), "Company cannot be null");
                }
                _context.companies.Add(company);
                _context.SaveChanges();
            }
            catch (Exception ex)
            {
                throw new Exception("An error occurred while creating the company", ex);
            }

        }

        public List<Company> GetAllCompanies()
        {
            try
            {
                return _context.companies.ToList();
            }
            catch (Exception ex)
            {
                // Log the exception (not implemented here)
                throw new Exception("An error occurred while retrieving companies", ex);
            }
        }

        public Company GetCompanyById(int id)
        {
            try
            {
                return _context.companies.FirstOrDefault(c => c.ID == id);
            }
            catch (Exception ex)
            {
                // Log the exception (not implemented here)
                throw new Exception($"An error occurred while retrieving the company with ID {id}", ex);
            }
        }
        public void DeleteCompany(int id)
        {
            try
            {
                var company = _context.companies.FirstOrDefault(c => c.ID == id);
                if (company == null)
                {
                    throw new KeyNotFoundException($"Company with ID {id} not found");
                }
                _context.companies.Remove(company);
                _context.SaveChanges();
            }
            catch (Exception ex)
            {
                // Log the exception (not implemented here)
                throw new Exception($"An error occurred while deleting the company with ID {id}", ex);
            }
        }
    }
}
