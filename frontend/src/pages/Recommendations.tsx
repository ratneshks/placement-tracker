import React, { useState } from 'react';
import api from '../lib/api';
import { Lightbulb, Target, Navigation } from 'lucide-react';

export default function Recommendations() {
  const [skills, setSkills] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState('');

  const handleGetRecommendations = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!skills) return;

    setLoading(true);
    setError('');
    
    const skillsList = skills.split(',').map(s => s.trim()).filter(s => s);

    try {
      const response = await api.post('/recommendations', skillsList);
      setResult(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to get recommendations');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      <div className="glass-panel p-8 text-center">
        <Lightbulb className="w-16 h-16 text-accent mx-auto mb-4" />
        <h2 className="text-3xl font-bold mb-4">Smart Recommendations</h2>
        <p className="text-gray-400 max-w-2xl mx-auto mb-8">
          Our recommendation engine analyzes your skills and past application success rates to suggest the best roles and companies for you to target next.
        </p>

        <form onSubmit={handleGetRecommendations} className="max-w-xl mx-auto flex flex-col gap-4">
          <input
            type="text"
            className="input-field text-center text-lg py-4"
            placeholder="Enter skills (e.g., Java, React, SQL)"
            value={skills}
            onChange={(e) => setSkills(e.target.value)}
            required
          />
          <button type="submit" className="btn-primary py-3 text-lg" disabled={loading || !skills}>
            {loading ? 'Analyzing Profile...' : 'Get My Recommendations'}
          </button>
        </form>

        {error && <p className="text-danger mt-4">{error}</p>}
      </div>

      {result && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="glass-panel p-8">
            <h3 className="text-xl font-bold mb-6 flex items-center border-b border-white/10 pb-4">
              <Target className="mr-3 text-primary" /> Roles to Target
            </h3>
            <ul className="space-y-4">
              {result.suitableRoles?.map((role: string, idx: number) => (
                <li key={idx} className="flex items-center p-3 bg-white/5 rounded-lg border border-white/5 hover:bg-white/10 transition-colors">
                  <div className="h-8 w-8 rounded-full bg-primary/20 text-primary flex items-center justify-center font-bold mr-4">
                    {idx + 1}
                  </div>
                  <span className="font-medium text-lg">{role}</span>
                </li>
              ))}
            </ul>
          </div>

          <div className="glass-panel p-8">
            <h3 className="text-xl font-bold mb-6 flex items-center border-b border-white/10 pb-4">
              <Navigation className="mr-3 text-accent" /> Recommended Companies
            </h3>
            <ul className="space-y-4">
              {result.companiesToTarget?.map((company: string, idx: number) => (
                <li key={idx} className="flex items-center p-3 bg-white/5 rounded-lg border border-white/5 hover:bg-white/10 transition-colors">
                  <div className="h-8 w-8 rounded-full bg-accent/20 text-accent flex items-center justify-center font-bold mr-4">
                    {idx + 1}
                  </div>
                  <span className="font-medium text-lg">{company}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      )}
    </div>
  );
}
