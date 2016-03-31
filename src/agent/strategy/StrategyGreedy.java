package agent.strategy;

import java.util.List;
import java.util.Random;

import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Etat;

/**
 * Strategie qui renvoit une action aleatoire avec probabilite epsilon, une
 * action gloutonne (qui suit la politique de l'agent) sinon Cette classe a
 * acces a un RLAgent par l'intermediaire de sa classe mere.
 *
 * @author lmatignon
 * @author Bruno Buiret (bruno.buiret@etu.univ-lyon1.fr)
 * @author Thomas Arnaud (thomas.arnaud@etu.univ-lyon1.fr)
 *
 */
public class StrategyGreedy extends StrategyExploration
{
    /**
     * 
     */
    protected double epsilon;

    /**
     * 
     */
    protected Random rand = new Random();

    /**
     * 
     * @param agent
     * @param epsilon 
     */
    public StrategyGreedy(RLAgent agent, double epsilon)
    {
        super(agent);
        this.epsilon = epsilon;
    }

    /**
     * @param _e
     * @return action selectionnee par la strategie d'exploration
     */
    @Override
    public Action getAction(Etat _e)
    {
        //getAction renvoi null si _e absorbant
        if(this.agent.getEnv().estAbsorbant())
        {
            return null;
        }

        if(rand.nextDouble() < this.epsilon)
        {
            List<Action> allActions = this.agent.getActionsLegales(_e);

            return allActions.get(rand.nextInt(allActions.size()));
        }
        else
        {
            List<Action> politique = this.agent.getPolitique(_e);
            
            return politique.size() > 0 ? politique.get(0) : null;
        }
    }

    /**
     * 
     * @param epsilon 
     */
    public void setEpsilon(double epsilon)
    {
        this.epsilon = epsilon;
    }
}
