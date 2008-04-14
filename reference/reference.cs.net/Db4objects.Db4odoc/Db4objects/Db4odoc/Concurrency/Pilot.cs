/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;

namespace Db4objects.Db4odoc.Concurrency
{
    public class Pilot
    {
        private String _name;
        private int _points;

        public Pilot(String name, int points)
        {
            _name = name;
            _points = points;
        }

        public String Name
        {
            get
            {
                return _name;
            }
            set
            {
                _name = value;
            }
        }

        public void AddPoints(int points)
        {
            _points += points;
        }

        public override string ToString()
        {
            return string.Format("{0}/{1}",_name, _points);
        }
    }

}
