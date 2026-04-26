import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../lib/api';
import { Plus, Edit2, Trash2 } from 'lucide-react';

export default function Applications() {
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({ companyName: '', role: '', status: 'APPLIED', appliedDate: new Date().toISOString().split('T')[0] });
  const queryClient = useQueryClient();

  const { data: applications, isLoading } = useQuery({
    queryKey: ['applications'],
    queryFn: async () => {
      const { data } = await api.get('/applications');
      return data;
    }
  });

  const addMutation = useMutation({
    mutationFn: (newApp: any) => api.post('/applications', newApp),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['applications'] });
      setShowModal(false);
      setFormData({ companyName: '', role: '', status: 'APPLIED', appliedDate: new Date().toISOString().split('T')[0] });
    }
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => api.delete(`/applications/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['applications'] })
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    addMutation.mutate(formData);
  };

  const getStatusColor = (status: string) => {
    switch(status) {
      case 'SELECTED': return 'bg-success/20 text-success border-success/30';
      case 'REJECTED': return 'bg-danger/20 text-danger border-danger/30';
      case 'INTERVIEW': return 'bg-warning/20 text-warning border-warning/30';
      case 'OA': return 'bg-primary/20 text-primary border-primary/30';
      default: return 'bg-gray-500/20 text-gray-400 border-gray-500/30';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Your Applications</h2>
        <button onClick={() => setShowModal(true)} className="btn-primary flex items-center">
          <Plus className="w-4 h-4 mr-2" /> Add Application
        </button>
      </div>

      {isLoading ? (
        <div className="space-y-4 animate-pulse">
          {[1,2,3].map(i => <div key={i} className="h-20 glass-panel" />)}
        </div>
      ) : applications?.length === 0 ? (
        <div className="glass-panel p-12 flex flex-col items-center justify-center text-center">
          <div className="h-24 w-24 bg-secondary/50 rounded-full flex items-center justify-center mb-4">
            <BriefcaseIcon className="w-12 h-12 text-gray-500" />
          </div>
          <h3 className="text-xl font-medium mb-2">No applications yet</h3>
          <p className="text-gray-400 mb-6">Start tracking your placements by adding your first application.</p>
          <button onClick={() => setShowModal(true)} className="btn-primary">Add Now</button>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-4">
          {applications?.map((app: any) => (
            <div key={app.id} className="glass-panel p-5 flex items-center justify-between hover:bg-white/5 transition-colors group">
              <div className="flex flex-col md:flex-row md:items-center gap-4">
                <div className="w-48">
                  <h4 className="font-bold text-lg">{app.companyName}</h4>
                  <p className="text-sm text-gray-400">{app.role}</p>
                </div>
                <div className="flex items-center gap-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold border ${getStatusColor(app.status)}`}>
                    {app.status}
                  </span>
                  <span className="text-sm text-gray-500">Applied: {app.appliedDate}</span>
                </div>
              </div>
              
              <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                <button className="p-2 text-gray-400 hover:text-primary transition-colors bg-white/5 rounded-lg">
                  <Edit2 className="w-4 h-4" />
                </button>
                <button 
                  onClick={() => deleteMutation.mutate(app.id)}
                  className="p-2 text-gray-400 hover:text-danger transition-colors bg-white/5 rounded-lg"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <div className="glass-panel w-full max-w-md p-6 bg-secondary">
            <h3 className="text-xl font-bold mb-4">Add Application</h3>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm mb-1">Company</label>
                <input required type="text" className="input-field" value={formData.companyName} onChange={e => setFormData({...formData, companyName: e.target.value})} />
              </div>
              <div>
                <label className="block text-sm mb-1">Role</label>
                <input required type="text" className="input-field" value={formData.role} onChange={e => setFormData({...formData, role: e.target.value})} />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm mb-1">Status</label>
                  <select className="input-field" value={formData.status} onChange={e => setFormData({...formData, status: e.target.value})}>
                    <option value="APPLIED">Applied</option>
                    <option value="OA">OA</option>
                    <option value="INTERVIEW">Interview</option>
                    <option value="SELECTED">Selected</option>
                    <option value="REJECTED">Rejected</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm mb-1">Date</label>
                  <input required type="date" className="input-field" value={formData.appliedDate} onChange={e => setFormData({...formData, appliedDate: e.target.value})} />
                </div>
              </div>
              <div className="flex gap-3 pt-4">
                <button type="button" onClick={() => setShowModal(false)} className="btn-secondary flex-1">Cancel</button>
                <button type="submit" className="btn-primary flex-1" disabled={addMutation.isPending}>
                  {addMutation.isPending ? 'Saving...' : 'Save'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

function BriefcaseIcon(props: any) {
  return <svg {...props} xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect width="20" height="14" x="2" y="7" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>;
}
