import React, { useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import api from '../lib/api';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import { Briefcase, CheckCircle, XCircle, Clock } from 'lucide-react';

export default function Dashboard() {
  const { data: analytics, isLoading, error } = useQuery({
    queryKey: ['analytics'],
    queryFn: async () => {
      const { data } = await api.get('/analytics');
      return data;
    }
  });

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 animate-pulse">
        {[1, 2, 3, 4].map(i => (
          <div key={i} className="h-32 glass-panel" />
        ))}
        <div className="col-span-full md:col-span-2 h-96 glass-panel" />
        <div className="col-span-full md:col-span-2 h-96 glass-panel" />
      </div>
    );
  }

  if (error) return <div className="text-danger">Failed to load analytics</div>;

  const weakAreasData = analytics?.weakAreas 
    ? Object.entries(analytics.weakAreas).map(([name, count]) => ({ name, count }))
    : [];

  const statCards = [
    { title: 'Total Applications', value: analytics?.totalApplications || 0, icon: Briefcase, color: 'text-primary', bg: 'bg-primary/20' },
    { title: 'Interviews', value: analytics?.totalInterviews || 0, icon: Clock, color: 'text-warning', bg: 'bg-warning/20' },
    { title: 'Offers', value: analytics?.totalOffers || 0, icon: CheckCircle, color: 'text-success', bg: 'bg-success/20' },
    { title: 'Rejections', value: analytics?.totalRejections || 0, icon: XCircle, color: 'text-danger', bg: 'bg-danger/20' },
  ];

  return (
    <div className="space-y-6">
      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statCards.map((stat, i) => (
          <div key={i} className="glass-panel p-6 flex items-center justify-between hover:scale-105 transition-transform duration-300">
            <div>
              <p className="text-gray-400 text-sm font-medium">{stat.title}</p>
              <p className="text-3xl font-bold mt-2">{stat.value}</p>
            </div>
            <div className={`h-12 w-12 rounded-full flex items-center justify-center ${stat.bg} ${stat.color}`}>
              <stat.icon className="h-6 w-6" />
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Selection Rate */}
        <div className="glass-panel p-6">
          <h3 className="text-lg font-semibold mb-4">Selection Rate</h3>
          <div className="flex flex-col items-center justify-center h-64">
            <div className="relative h-40 w-40">
              <svg className="w-full h-full transform -rotate-90">
                <circle cx="80" cy="80" r="70" fill="none" stroke="currentColor" strokeWidth="10" className="text-secondary" />
                <circle 
                  cx="80" cy="80" r="70" fill="none" stroke="currentColor" strokeWidth="10" 
                  className="text-success"
                  strokeDasharray={`${(analytics?.selectionRate || 0) * 4.39} 439`}
                  strokeLinecap="round"
                />
              </svg>
              <div className="absolute inset-0 flex flex-col items-center justify-center">
                <span className="text-3xl font-bold text-success">{analytics?.selectionRate || 0}%</span>
              </div>
            </div>
            <p className="mt-4 text-gray-400 text-sm">Overall conversion to offer</p>
          </div>
        </div>

        {/* Weak Areas Chart */}
        <div className="glass-panel p-6">
          <h3 className="text-lg font-semibold mb-4">Interview Weak Areas</h3>
          {weakAreasData.length > 0 ? (
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={weakAreasData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#ffffff20" vertical={false} />
                  <XAxis dataKey="name" stroke="#9ca3af" tick={{ fill: '#9ca3af' }} />
                  <YAxis stroke="#9ca3af" tick={{ fill: '#9ca3af' }} allowDecimals={false} />
                  <Tooltip 
                    cursor={{ fill: 'rgba(255,255,255,0.05)' }} 
                    contentStyle={{ backgroundColor: '#1e293b', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px' }}
                  />
                  <Bar dataKey="count" fill="#ef4444" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          ) : (
            <div className="flex items-center justify-center h-64 text-gray-500">
              Not enough data to identify weak areas. Log more interviews!
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
